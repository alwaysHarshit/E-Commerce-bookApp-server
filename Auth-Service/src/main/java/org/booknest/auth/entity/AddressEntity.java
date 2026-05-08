package org.booknest.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Address Entity")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Address ID", example = "1")
    private Long addressId;

    @Schema(description = "Primary address line", example = "123 Main St")
    private String addressLine1;

    @Schema(description = "Secondary address line", example = "Apt 4B")
    private String addressLine2;

    @Schema(description = "Landmark nearby", example = "Opposite Central Park")
    private String landmark;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "State", example = "NY")
    private String state;

    @Schema(description = "Country", example = "USA")
    private String country;

    @Schema(description = "Postal code", example = "10001")
    private String postalCode;

    @Schema(description = "Is this the default address", example = "true")
    private boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Schema(hidden = true)
    private UserEntity user;

}
