package org.booknest.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.booknest.orderservice.enums.OrderStatus;
import org.booknest.orderservice.enums.PaymentStatus;
import org.booknest.orderservice.enums.PaymentType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double totalAmount;

    private Long shippingAddressId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;


    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private Long paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;



    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)

    @Builder.Default
    @JsonBackReference
    private List<OrderItemEntity> items = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}