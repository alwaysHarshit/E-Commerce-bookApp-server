package org.booknest.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.enums.Role;
import org.booknest.auth.model.LoginRequest;
import org.booknest.auth.model.LoginResponse;
import org.booknest.auth.model.RegisterRequest;
import org.booknest.auth.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User Login", description = "Authenticates a user and returns a JWT token")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<org.booknest.auth.model.ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login request received for user: {}", loginRequest.getEmail());
        org.booknest.auth.model.ApiResponse<LoginResponse> apiResponse = authService.login(loginRequest);
        log.info("Login successful, token generated for user: {}", loginRequest.getEmail());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "User Registration", description = "Registers a new user and sends an OTP to their email")
    @ApiResponse(responseCode = "200", description = "Registration initiated, OTP sent")
    @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    @PostMapping("/register")
    public ResponseEntity<org.booknest.auth.model.ApiResponse<Void>> register(@RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for user: {}", registerRequest.getEmail());
        authService.register(registerRequest, Role.USER);
        return ResponseEntity.ok(org.booknest.auth.model.ApiResponse.success("Otp Sent successfully", null));
    }

    @Operation(summary = "Admin Registration", description = "Registers a new admin user. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Admin registration initiated, OTP sent")
    @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<org.booknest.auth.model.ApiResponse<Void>> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        log.info("Registration Admin request received for user: {}", registerRequest.getEmail());
        authService.register(registerRequest, Role.ADMIN);
        return ResponseEntity.ok(org.booknest.auth.model.ApiResponse.success("Otp Sent successfully", null));
    }

    @Operation(summary = "Verify OTP", description = "Verifies the OTP sent during registration")
    @ApiResponse(responseCode = "200", description = "OTP verified successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    @PostMapping("/verify-otp")
    public ResponseEntity<org.booknest.auth.model.ApiResponse<String>> verifyOtp(@RequestParam("otp") String otp, @RequestParam("email") String email) {
        log.info("OTP verification request received for user: {}", email);
        authService.verifyOtp(otp, email);
        return ResponseEntity.ok(org.booknest.auth.model.ApiResponse.success("Otp verified successfully", null));
    }

    @Operation(summary = "Add First Admin", description = "Endpoint to add the first admin user without existing admin role (for initial setup)")
    @ApiResponse(responseCode = "200", description = "Admin added successfully")
    @PostMapping("/test/addAdmin")
    public ResponseEntity<org.booknest.auth.model.ApiResponse<Void>> addNewAdminFirstTime(@RequestBody RegisterRequest registerRequest) {
        log.info("Add Admin request received for user: {}", registerRequest.getEmail());
        return this.registerAdmin(registerRequest);
    }
}
