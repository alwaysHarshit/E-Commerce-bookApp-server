package org.booknest.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.booknest.auth.dto.AddressRequestDTO;
import org.booknest.auth.entity.AddressEntity;
import org.booknest.auth.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/address")
@Tag(name = "Address Management", description = "Endpoints for managing user addresses")
public class AddressController {
    @Autowired
    AddressService  addressService;

    @Operation(summary = "Get All Addresses", description = "Returns a list of all addresses for the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved addresses")
    @GetMapping
    public ResponseEntity<List<AddressEntity>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @Operation(summary = "Get Address by ID", description = "Returns a specific address by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved address")
    @ApiResponse(responseCode = "404", description = "Address not found")
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressEntity> getAddressById(@PathVariable Long addressId) {
        AddressEntity addressById = addressService.getAddressById(addressId);
        return ResponseEntity.ok(addressById);
    }

    @Operation(summary = "Add Address", description = "Adds a new address for the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Address added successfully")
    @PostMapping
    public ResponseEntity<AddressEntity> addAddress(@RequestBody AddressRequestDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        AddressEntity addressEntity = addressService.addAddress(request, userDetails);
        return ResponseEntity.ok(addressEntity);
    }

    @Operation(summary = "Update Address", description = "Updates an existing address")
    @ApiResponse(responseCode = "200", description = "Address updated successfully")
    @ApiResponse(responseCode = "404", description = "Address not found")
    @PatchMapping("/{addressId}")
    public ResponseEntity<AddressEntity> updateAddress(@PathVariable Long addressId, @RequestBody AddressRequestDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        AddressEntity addressEntity = addressService.updateAddress(addressId, request, userDetails);
        return ResponseEntity.ok(addressEntity);
    }

    @Operation(summary = "Delete Address", description = "Deletes an existing address")
    @ApiResponse(responseCode = "200", description = "Address deleted successfully")
    @ApiResponse(responseCode = "404", description = "Address not found")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal UserDetails userDetails) {
        addressService.deleteAddress(addressId,userDetails);
        return ResponseEntity.ok("Successfully deleted the address");
    }

}
