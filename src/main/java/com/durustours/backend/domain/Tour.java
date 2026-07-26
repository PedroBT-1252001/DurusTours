package com.durustours.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourCategory category;

    @NotBlank(message = "Tour name is required")
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Column(nullable = false)
    private Integer durationMinutes;

    @NotNull(message = "Adult price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Adult price cannot be negative")
    @Column(nullable = false)
    private BigDecimal priceAdult;

    @NotNull(message = "Child price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Child price cannot be negative")
    @Column(nullable = false)
    private BigDecimal priceChild;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    public boolean requiresComboActivation() {
        return category != null && category.requiresComboActivation();
    }
}
