package org.booknest.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.booknest.auth.enums.Provider;
import org.booknest.auth.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Schema(description = "User Entity")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User ID", example = "1")
    private Long id;

    @Column(unique = true)
    @Schema(description = "User email address", example = "user@example.com")
    private String email;

    @Schema(hidden = true)
    private String password;

    @Schema(description = "User display name", example = "John Doe")
    private String name;

    @Schema(description = "User phone number", example = "+1234567890")
    private  String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Authentication provider", example = "LOCAL")
    private Provider provider;     // GOOGLE, GITHUB, LOCAL

    @Schema(description = "Provider specific ID", example = "12345")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Schema(description = "User role", example = "USER")
    private Role role;  // admin , user

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "List of user addresses")
    private List<AddressEntity> adresses=new ArrayList<>();

    @Schema(description = "Is user email verified", example = "true")
    private boolean isVerified;

    @Schema(hidden = true)
    private String otp;

    @Schema(hidden = true)
    private LocalDateTime otpExpiry;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

}
