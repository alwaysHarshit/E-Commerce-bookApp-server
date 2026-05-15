package org.booknest.paymentservice.Client;

import org.booknest.paymentservice.dto.PaymentStatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ORDER-SERVICE")
@Component
public interface OrderClient {

    @PutMapping( "/order-service/api/internal/orders/{orderId}/payment-status")
    void updatePaymentStatus(@PathVariable Long orderId, @RequestBody PaymentStatusUpdateRequest request);
}