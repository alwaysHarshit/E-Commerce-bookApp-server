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
import org.booknest.paymentservice.execptions.PaymentObejctNotFound;
import org.booknest.paymentservice.execptions.StripeExecption;
import org.booknest.paymentservice.mapper.PaymentMapper;
import org.booknest.paymentservice.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

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
        PaymentIntentCreateParams build = PaymentIntentCreateParams.builder().setAmount((long) paymentRequestDto.getAmount() * 100).setCurrency(paymentRequestDto.getCurrency()).setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()).build();

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.create(build);

            // build the payment enity
            Payment paymentEntity = Payment.builder().orderId(paymentRequestDto.getOrderId()).paymentIntentId(paymentIntent.getId()).amount(paymentRequestDto.getAmount()).currency(paymentRequestDto.getCurrency()).status(PaymentStatus.INITIATED).build();

            //save in db
            Payment save = paymentRepository.save(paymentEntity);

            return PaymentIntentResponseDto.builder()
                    .paymentId(save.getPaymentId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .status(PaymentStatus.INITIATED)
                    .build();


        } catch (StripeException e) {
            throw new StripeExecption(e.getMessage());
        }
    }


    @Override
    public String handleStripeWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            //building the payment intent object
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new StripeExecption("Payment Intent object not found"));

            // get the payment enity from db to update its state
            Payment paymentEnity = paymentRepository.findByPaymentIntentId(paymentIntent.getId())
                    .orElseThrow(() -> new PaymentObejctNotFound("Payment object not found in db"));

            switch (event.getType()) {

                case "payment_intent.succeeded":

                    paymentEnity.setStatus(PaymentStatus.SUCCESS);
                    paymentRepository.save(paymentEnity);

                    // call the order service to update about payment sucess

                    orderClient.updatePaymentStatus(
                            paymentEnity.getOrderId(),
                            new PaymentStatusUpdateRequest(PaymentStatus.SUCCESS)

                    );


                    break;

                case "payment_intent.payment_failed":
                    paymentEnity.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(paymentEnity);

                    // call the order service to update about failer
                    orderClient.updatePaymentStatus(
                            paymentEnity.getOrderId(),
                            new PaymentStatusUpdateRequest(PaymentStatus.FAILED)
                    );
                    break;

                default:
                    log.info("Unhandled event type {}", event.getType());
            }

            return "Webhook processed successfully";
        } catch (Exception e) {
            throw new StripeExecption("Something went wrong in Event handling " + e.getMessage());
        }
    }

    @Override
    public PaymentResponseDto getPaymentByOrderId(Long orderId) {

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new PaymentObejctNotFound("Payment object not found in db"));

        return paymentMapper.mapToPaymentReponse(payment);
    }

    @Override
    public String cancelPayment(String paymentIntentId) {

        // first check the payment intent in db and get it back
        Payment paymentEnity = paymentRepository
                .findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found with order id "
                                        + paymentIntentId
                        ));

        try {

            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentEnity.getPaymentIntentId());

            paymentIntent.cancel();

            //save the updated state
            paymentEnity.setStatus(PaymentStatus.CANCELED);
            paymentRepository.save(paymentEnity);

            //call the order service
            orderClient.updatePaymentStatus(
                    paymentEnity.getOrderId(),
                    new PaymentStatusUpdateRequest(PaymentStatus.CANCELED)
            );

            return "Payment cancelled successfully";

        }
        catch (StripeException e) {
            throw new StripeExecption("Failed to cancel payment : " + e.getMessage());
        }
    }

    @Override
    public Page<PaymentResponseDto> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(paymentMapper::mapToPaymentReponse);
    }
}
