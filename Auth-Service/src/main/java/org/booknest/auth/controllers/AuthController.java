package org.booknest.auth.controllers;

import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.entity.Role;
import org.booknest.auth.model.ApiResponse;
import org.booknest.auth.model.LoginRequest;
import org.booknest.auth.model.LoginResponse;
import org.booknest.auth.model.RegisterRequest;
import org.booknest.auth.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login request received for user: {}", loginRequest.getEmail());
        ApiResponse<LoginResponse> apiResponse = authService.login(loginRequest);
        log.info("Login successful, token generated for user: {}", loginRequest.getEmail());
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for user: {}", registerRequest.getEmail());
        authService.register(registerRequest, Role.USER);
        return ResponseEntity.ok(ApiResponse.success("Otp Sent successfully", null));
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        log.info("Registration Admin request received for user: {}", registerRequest.getEmail());
        authService.register(registerRequest, Role.ADMIN);
        return ResponseEntity.ok(ApiResponse.success("Otp Sent successfully", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam("otp") String otp, @RequestParam("email") String email) {
        log.info("OTP verification request received for user: {}", email);
        authService.verifyOtp(otp, email);
        return ResponseEntity.ok(ApiResponse.success("Otp verified successfully", null));
    }

    @PostMapping("/test/addAdmin")
    public ResponseEntity<ApiResponse<Void>> addNewAdminFirstTime(@RequestBody RegisterRequest registerRequest) {
        log.info("Add Admin request received for user: {}", registerRequest.getEmail());
        return this.registerAdmin(registerRequest);
    }
}
