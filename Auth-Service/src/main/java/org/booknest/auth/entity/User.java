package org.booknest.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private String password;

    private String provider;     // GOOGLE, GITHUB, LOCAL
    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;  // admin , user

    private boolean isVerified;

    private String otp;

    private LocalDateTime otpExpiry;

    private LocalDateTime createdAt;

}
