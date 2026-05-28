package org.booknest.catelogservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.booknest.catelogservice.model.ApiResponse;
import org.booknest.catelogservice.services.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Controller", description = "Endpoints for managing book inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Check stock availability")
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkStock(@RequestParam Long bookId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.checkStock(bookId, quantity));
    }

    @Operation(summary = "Reduce stock")
    @PutMapping("/reduce")
    public ResponseEntity<ApiResponse<Void>> reduceStock(@RequestParam Long bookId, @RequestParam Integer quantity) {
        inventoryService.reduceStock(bookId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock reduced successfully", null));
    }

    @Operation(summary = "Restore stock")
    @PutMapping("/restore")
    public ResponseEntity<ApiResponse<Void>> restoreStock(@RequestParam Long bookId, @RequestParam Integer quantity) {
        inventoryService.restoreStock(bookId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock restored successfully", null));
    }

    @Operation(summary = "Update stock manually")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateStock(@RequestParam Long bookId, @RequestParam Integer quantity) {
        inventoryService.updateStock(bookId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", null));
    }
}
