package org.booknest.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.dto.LoginRequestDto;
import org.booknest.auth.dto.RegisterRequestDto;
import org.booknest.auth.utils.CustomUser;
import org.booknest.auth.utils.EmailService;
import org.booknest.auth.utils.JwtUtils;
import org.booknest.auth.entity.UserEntity;
import org.booknest.auth.enums.Provider;
import org.booknest.auth.enums.Role;
import org.booknest.auth.exception.EmailAlreadyExistsException;
import org.booknest.auth.exception.InvalidOtpException;
import org.booknest.auth.exception.OtpExpiredException;
import org.booknest.auth.model.*;
import org.booknest.auth.repo.UserRepo;
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

    private final UserRepo userRepo;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepo userRepo, EmailService emailService,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<LoginResponse> login(LoginRequestDto loginRequestDto) {
        log.info("Attempting login for user: {}", loginRequestDto.getEmail());
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );
        log.debug("Authentication result: {}", authenticate.getPrincipal());

        if (authenticate.isAuthenticated()) {
            log.info("Authentication successful for {}", loginRequestDto.getEmail());

            CustomUser user = (CustomUser) authenticate.getPrincipal();

            log.debug("user: {}", user);


            String role = user.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.replace("ROLE_", ""))
                    .orElse("USER");

            String token = jwtUtils.generateToken(user.getId(), role);

            LoginResponse loginResponse = new LoginResponse(token, user.getEmail(), user.getRole(), user.getName());
            return ApiResponse.success("Succesfully logged in ", loginResponse);


        } else {
            log.error("Authentication failed for {}", loginRequestDto.getEmail());
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    public void register(RegisterRequestDto registerRequestDto, Role role) {
        log.info("Processing registration for email: {}", registerRequestDto.getEmail());
        if (userRepo.existsByEmail(registerRequestDto.getEmail())) {
            log.warn("Registration failed: Email {} already exists", registerRequestDto.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String otp = generateOtp();
        log.debug("Generated OTP for {}: {}", registerRequestDto.getEmail(), otp);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(registerRequestDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        userEntity.setName(registerRequestDto.getName());
        userEntity.setProvider(Provider.LOCAL);
        userEntity.setVerified(false);
        userEntity.setOtp(otp);
        userEntity.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userEntity.setCreatedAt(LocalDateTime.now());

        userEntity.setRole(role);

        userRepo.save(userEntity);

        log.info("User {} saved to DB (unverified)", registerRequestDto.getEmail());

        emailService.sendEmail(registerRequestDto.getEmail(), otp);
    }

    public void verifyOtp(String otp, String email) {
        log.info("Verifying OTP for email: {}", email);
        UserEntity dbUserEntity = userRepo.findByEmail(email).orElseThrow(() -> {
            log.error("OTP Verification failed: User {} not found", email);
            return new UsernameNotFoundException("User not found");
        });

        if (dbUserEntity.getOtpExpiry().isBefore(LocalDateTime.now())) {
            log.warn("OTP Verification failed for {}: OTP expired", email);
            throw new OtpExpiredException("Otp is expired");
        }

        if (!dbUserEntity.getOtp().equals(otp)) {
            log.warn("OTP Verification failed for {}: Invalid OTP", email);
            throw new InvalidOtpException("Otp is Invalid");
        }

        dbUserEntity.setVerified(true);
        dbUserEntity.setOtp(null);
        dbUserEntity.setOtpExpiry(null);
        userRepo.save(dbUserEntity);
        log.info("User {} successfully verified", email);

    }
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }


}
