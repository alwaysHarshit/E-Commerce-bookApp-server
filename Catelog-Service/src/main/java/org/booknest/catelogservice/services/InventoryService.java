package org.booknest.catelogservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.booknest.catelogservice.entity.Book;
import org.booknest.catelogservice.entity.Inventory;
import org.booknest.catelogservice.exceptions.ResourceNotFoundException;
import org.booknest.catelogservice.repo.InventoryRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepo inventoryRepo;

    @Transactional(readOnly = true)
    public boolean checkStock(Long bookId, Integer quantity) {
        Inventory inventory = inventoryRepo.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for book id: " + bookId));
        return inventory.getStock() >= quantity;
    }

    @Transactional
    public void reduceStock(Long bookId, Integer quantity) {
        Inventory inventory = inventoryRepo.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for book id: " + bookId));
        
        if (inventory.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for book id: " + bookId);
        }
        
        inventory.setStock(inventory.getStock() - quantity);
        if (inventory.getStock() == 0) {
            inventory.setStatus("OUT_OF_STOCK");
        }
        inventoryRepo.save(inventory);
        log.info("Reduced stock for book id: {} by {}", bookId, quantity);
    }

    @Transactional
    public void restoreStock(Long bookId, Integer quantity) {
        Inventory inventory = inventoryRepo.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for book id: " + bookId));
        
        inventory.setStock(inventory.getStock() + quantity);
        if (inventory.getStock() > 0) {
            inventory.setStatus("AVAILABLE");
        }
        inventoryRepo.save(inventory);
        log.info("Restored stock for book id: {} by {}", bookId, quantity);
    }

    @Transactional
    public void updateStock(Long bookId, Integer newStock) {
        Inventory inventory = inventoryRepo.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for book id: " + bookId));
        
        inventory.setStock(newStock);
        inventory.setStatus(newStock > 0 ? "AVAILABLE" : "OUT_OF_STOCK");
        inventoryRepo.save(inventory);
    }
}
