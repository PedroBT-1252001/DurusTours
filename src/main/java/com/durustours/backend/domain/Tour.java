package com.durustours.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tour category is required")
    @Convert(converter = TourCategoryConverter.class)
    @Column(nullable = false)
    private TourCategory category;

    @NotBlank(message = "Tour title is required")
    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    /** Exact minutes when known (e.g. 50 for river cruises); null for full-day tours. */
    @Positive(message = "Duration must be positive when specified")
    @Column
    private Integer durationMinutes;

    @NotBlank(message = "Duration label is required")
    @Column(nullable = false)
    private String durationLabel;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base price cannot be negative")
    @Column(nullable = false)
    private BigDecimal basePrice;

    @Builder.Default
    @Column(nullable = false)
    private boolean combo = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    public boolean requiresComboActivation() {
        return combo;
    }
}
