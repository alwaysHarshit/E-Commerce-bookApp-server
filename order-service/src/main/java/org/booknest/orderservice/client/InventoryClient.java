package org.booknest.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PutMapping("/api/inventory/reduce")
    void reduceStock(@RequestParam Long bookId, @RequestParam Integer quantity);

    @PutMapping("/api/inventory/restore")
    void restoreStock(@RequestParam Long bookId, @RequestParam Integer quantity);
    
    @GetMapping("/api/inventory/check")
    boolean checkStock(@RequestParam Long bookId, @RequestParam Integer quantity);
}
