package org.booknest.orderservice.repo;

import org.booknest.orderservice.entity.OrderEntity;
import org.booknest.orderservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // For admin pagination
    Page<OrderEntity> findAll(Pageable pageable);

    // Internal check: Has user purchased and received this book?
    boolean existsByUserIdAndOrderStatusAndItems_BookId(Long userId, OrderStatus status, Long bookId);
}
