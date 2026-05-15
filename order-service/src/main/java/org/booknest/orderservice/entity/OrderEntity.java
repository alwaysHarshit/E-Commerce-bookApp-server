package org.booknest.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.booknest.orderservice.enums.OrderStatus;
import org.booknest.orderservice.enums.PaymentStatus;
import org.booknest.orderservice.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Double totalAmount;
    private Long shippingAddressId;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Long paymentId;
    private String paymentIntentId;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime expiresAt;
    private Double paidAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
