package org.booknest.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Login Request Payload")
public class LoginRequestDto {
    @Schema(description = "User email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(description = "User password", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
