package org.booknest.paymentservice.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.booknest.paymentservice.Client.OrderClient;
import org.booknest.paymentservice.dto.PaymentIntentResponseDto;
import org.booknest.paymentservice.dto.PaymentRequestDto;
import org.booknest.paymentservice.dto.PaymentResponseDto;
import org.booknest.paymentservice.dto.PaymentStatusUpdateRequest;
import org.booknest.paymentservice.entity.Payment;
import org.booknest.paymentservice.enums.PaymentStatus;
import org.booknest.paymentservice.exception.PaymentNotFoundException;
import org.booknest.paymentservice.exception.StripeServiceException;
import org.booknest.paymentservice.mapper.PaymentMapper;
import org.booknest.paymentservice.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    final PaymentRepository paymentRepository;
    final PaymentMapper paymentMapper;
    final OrderClient orderClient;


    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper, OrderClient orderClient) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.orderClient = orderClient;
    }

    @Override
    public PaymentIntentResponseDto createPaymentIntent(PaymentRequestDto paymentRequestDto) {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (paymentRequestDto.getAmount() * 100))
                .setCurrency(paymentRequestDto.getCurrency())
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .build();

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Payment paymentEntity = Payment.builder()
                    .orderId(paymentRequestDto.getOrderId())
                    .paymentIntentId(paymentIntent.getId())
                    .amount(paymentRequestDto.getAmount())
                    .currency(paymentRequestDto.getCurrency())
                    .status(PaymentStatus.INITIATED)
                    .build();

            Payment savedPayment = paymentRepository.save(paymentEntity);

            return PaymentIntentResponseDto.builder()
                    .paymentId(savedPayment.getPaymentId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .status(PaymentStatus.INITIATED)
                    .build();

        } catch (StripeException e) {
            log.error("Stripe error during intent creation: {}", e.getMessage());
            throw new StripeServiceException("Failed to create Stripe payment intent: " + e.getMessage());
        }
    }


    @Override
    public String handleStripeWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new StripeServiceException("Payment Intent object not found in webhook payload"));

            Payment paymentEntity = paymentRepository.findByPaymentIntentId(paymentIntent.getId())
                    .orElseThrow(() -> new PaymentNotFoundException("Payment record not found for intent ID: " + paymentIntent.getId()));

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    paymentEntity.setStatus(PaymentStatus.SUCCESS);
                    paymentRepository.save(paymentEntity);
                    orderClient.updatePaymentStatus(paymentEntity.getOrderId(), new PaymentStatusUpdateRequest(PaymentStatus.SUCCESS));
                    break;

                case "payment_intent.payment_failed":
                    paymentEntity.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(paymentEntity);
                    orderClient.updatePaymentStatus(paymentEntity.getOrderId(), new PaymentStatusUpdateRequest(PaymentStatus.FAILED));
                    break;

                default:
                    log.info("Unhandled Stripe event type: {}", event.getType());
            }

            return "Webhook processed successfully";
        } catch (Exception e) {
            log.error("Webhook processing failed: {}", e.getMessage());
            throw new StripeServiceException("Error handling Stripe webhook: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponseDto getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order ID: " + orderId));
        return paymentMapper.mapToPaymentReponse(payment);
    }

    @Override
    public String cancelPayment(String paymentIntentId) {
        Payment paymentEntity = paymentRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment record not found for intent ID: " + paymentIntentId));

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentEntity.getPaymentIntentId());
            paymentIntent.cancel();

            paymentEntity.setStatus(PaymentStatus.CANCELED);
            paymentRepository.save(paymentEntity);

            orderClient.updatePaymentStatus(paymentEntity.getOrderId(), new PaymentStatusUpdateRequest(PaymentStatus.CANCELED));

            return "Payment cancelled successfully";
        } catch (StripeException e) {
            log.error("Stripe error during payment cancellation: {}", e.getMessage());
            throw new StripeServiceException("Failed to cancel Stripe payment: " + e.getMessage());
        }
    }

    @Override
    public Page<PaymentResponseDto> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(paymentMapper::mapToPaymentReponse);
    }
}
