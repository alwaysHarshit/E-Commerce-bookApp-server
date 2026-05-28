package org.booknest.catelogservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    private Book book;

    @Column(nullable = false)
    private int stock;

    private int lowStockThreshold;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private String status; // e.g., AVAILABLE, OUT_OF_STOCK, DISCONTINUED
}
