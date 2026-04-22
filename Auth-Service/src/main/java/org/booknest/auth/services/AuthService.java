package org.booknest.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.config.JwtUtils;
import org.booknest.auth.entity.Role;
import org.booknest.auth.entity.User;
import org.booknest.auth.exception.EmailAlreadyExistsException;
import org.booknest.auth.exception.InvalidOtpException;
import org.booknest.auth.exception.OtpExpiredException;
import org.booknest.auth.model.*;
import org.booknest.auth.repo.AuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthService {

    private final AuthRepo authRepo;
    private final EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    public AuthService(AuthRepo authRepo, EmailService emailService) {
        this.authRepo = authRepo;
        this.emailService = emailService;
    }

    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getEmail());
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        if (authenticate.isAuthenticated()) {
            log.info("Authentication successful for {}", loginRequest.getEmail());

            CustomUser user = (CustomUser) authenticate.getPrincipal();

            log.debug("user: {}", user);


            String role = user.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.replace("ROLE_", ""))
                    .orElse("USER");

            String token = jwtUtils.generateToken(user.getEmail(), role);

            LoginResponse loginResponse = new LoginResponse(token, user.getEmail(), user.getRole(), user.getName());
            return ApiResponse.success("Succesfully logged in ", loginResponse);


        } else {
            log.error("Authentication failed for {}", loginRequest.getEmail());
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    public void register(RegisterRequest registerRequest, Role role) {
        log.info("Processing registration for email: {}", registerRequest.getEmail());
        if (authRepo.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email {} already exists", registerRequest.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String otp = generateOtp();
        log.debug("Generated OTP for {}: {}", registerRequest.getEmail(), otp);

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setProvider("LOCAL");
        user.setVerified(false);
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setCreatedAt(LocalDateTime.now());

        user.setRole(role);

        authRepo.save(user);

        log.info("User {} saved to DB (unverified)", registerRequest.getEmail());

        emailService.sendEmail(registerRequest.getEmail(), otp);
    }

    public void verifyOtp(String otp, String email) {
        log.info("Verifying OTP for email: {}", email);
        User dbUser = authRepo.findByEmail(email).orElseThrow(() -> {
            log.error("OTP Verification failed: User {} not found", email);
            return new UsernameNotFoundException("User not found");
        });

        if (dbUser.getOtpExpiry().isBefore(LocalDateTime.now())) {
            log.warn("OTP Verification failed for {}: OTP expired", email);
            throw new OtpExpiredException("Otp is expired");
        }

        if (!dbUser.getOtp().equals(otp)) {
            log.warn("OTP Verification failed for {}: Invalid OTP", email);
            throw new InvalidOtpException("Otp is Invalid");
        }

        dbUser.setVerified(true);
        dbUser.setOtp(null);
        dbUser.setOtpExpiry(null);
        authRepo.save(dbUser);
        log.info("User {} successfully verified", email);

    }

    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }


}
