package org.booknest.catelogservice.repo;

import org.booknest.catelogservice.entity.Book;
import org.booknest.catelogservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByBook(Book book);
    Optional<Inventory> findByBookId(Long bookId);
}
