package org.booknest.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ItemId;

    private Long bookId;
    private String bookTitle;
    private  double price;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private OrderEntity orderE;


}
