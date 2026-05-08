package org.booknest.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.dto.UpdatePasswordDTO;
import org.booknest.auth.entity.UserEntity;
import org.booknest.auth.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/user")
@Tag(name = "User Management", description = "Endpoints for managing user profile and account")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get Current User", description = "Returns the details of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user details")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userService.getUser(userDetails);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update Password", description = "Updates the password for the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid old password or weak new password")
    @PatchMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails) {
        userService.updatePassword(requestDTO,userDetails);
        return ResponseEntity.ok("Success");
    }

    @Operation(summary = "Delete Account", description = "Deletes the account of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Account deleted successfully")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteAccount(userDetails);
        return ResponseEntity.ok("Successfully Deleted");
    }
}

