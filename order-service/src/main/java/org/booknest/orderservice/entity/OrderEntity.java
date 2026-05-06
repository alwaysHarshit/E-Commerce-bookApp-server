package org.booknest.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.booknest.orderservice.enums.OrderStatus;
import org.booknest.orderservice.enums.PaymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    @OneToMany(mappedBy = "orderE", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderedItems=new ArrayList<>();

    private LocalDateTime orderDatetime;

    private  double totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime updateDate;

}

