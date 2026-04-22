package org.booknest.auth.controllers;

import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.entity.Role;
import org.booknest.auth.model.ApiResponse;
import org.booknest.auth.model.LoginRequest;
import org.booknest.auth.model.LoginResponse;
import org.booknest.auth.model.RegisterRequest;
import org.booknest.auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login request received for user: {}", loginRequest.getEmail());
        ApiResponse<LoginResponse> apiResponse = authService.login(loginRequest);
        log.info("Login successful, token generated for user: {}", loginRequest.getEmail());
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest registerRequest){
         log.info("Registration request received for user: {}", registerRequest.getEmail());
         authService.register(registerRequest, Role.USER);
         return ResponseEntity.ok(ApiResponse.success("Otp Sent successfully", null));
    }
    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for user: {}", registerRequest.getEmail());
        authService.register(registerRequest, Role.ADMIN);
        return ResponseEntity.ok(ApiResponse.success("Otp Sent successfully", null));
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam("otp") String otp ,@RequestParam("email") String email){
        log.info("OTP verification request received for user: {}", email);
        authService.verifyOtp(otp, email);
        return ResponseEntity.ok(ApiResponse.success("Otp verified successfully", null));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetails>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching profile for user: {}", userDetails != null ? userDetails.getUsername() : "null");
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", userDetails));
    }

    @GetMapping("/gitProfile")
    public ResponseEntity<ApiResponse<OAuth2User>> getGitUser(@AuthenticationPrincipal OAuth2User userDetails) {
        log.info("Fetching GitHub profile");
        return ResponseEntity.ok(ApiResponse.success("GitHub profile fetched successfully", userDetails));
    }

}
