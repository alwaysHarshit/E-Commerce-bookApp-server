package org.booknest.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.booknest.auth.enums.Role;

@Getter
@Schema(description = "Login Response Payload")
public class LoginResponse {
    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    @Schema(description = "Token type", example = "Bearer")
    private String type = "Bearer";
    @Schema(description = "User email address", example = "user@example.com")
    private String email;
    @Schema(description = "User role", example = "USER")
    private Role role;
    @Schema(description = "User display name", example = "John Doe")
    private String name;

    public LoginResponse(String token, String email, Role role, String name) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.name = name;
    }
}
