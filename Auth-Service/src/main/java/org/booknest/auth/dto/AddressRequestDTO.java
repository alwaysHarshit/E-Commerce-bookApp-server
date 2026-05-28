package org.booknest.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Address Request DTO")
public class AddressRequestDTO {

    @Schema(description = "Primary address line", example = "123 Main St", requiredMode = Schema.RequiredMode.REQUIRED)
    private String addressLine1;

    @Schema(description = "Secondary address line", example = "Apt 4B")
    private String addressLine2;

    @Schema(description = "Landmark nearby", example = "Opposite Central Park")
    private String landmark;

    @Schema(description = "City", example = "New York", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @Schema(description = "State", example = "NY", requiredMode = Schema.RequiredMode.REQUIRED)
    private String state;

    @Schema(description = "Country", example = "USA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;

    @Schema(description = "Postal code", example = "10001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String postalCode;
}
