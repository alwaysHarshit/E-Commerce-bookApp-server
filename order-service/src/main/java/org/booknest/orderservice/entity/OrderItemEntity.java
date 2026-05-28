package org.booknest.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;
    private String bookTitle;
    private String isbn; // Snapshot of book ISBN
    private Double bookPrice; // Snapshot of price at purchase
    private Integer quantity;
    private Double subtotal; // bookPrice * quantity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private OrderEntity order;
}
