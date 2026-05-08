package org.booknest.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Update Password Request Payload")
public class UpdatePasswordDTO {

    @Schema(description = "Current password", example = "oldPass123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;
    @Schema(description = "New password", example = "newPass123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

}
