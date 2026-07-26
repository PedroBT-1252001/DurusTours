package com.durustours.backend.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    /** Burmester Combo voucher validity window, starting at first activation (dock or cellar). */
    public static final Duration COMBO_VALIDITY_PERIOD = Duration.ofHours(48);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Confirmation code is required")
    @Column(nullable = false, unique = true)
    private String confirmationCode;

    @NotNull(message = "Customer is required")
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Tour is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @NotNull(message = "Requested date/time slot is required")
    @Column(nullable = false)
    private LocalDateTime requestedDateTime;

    @NotNull(message = "Adult passenger count is required")
    @Min(value = 0, message = "Adult passenger count cannot be negative")
    @Column(nullable = false)
    private Integer adultCount;

    @NotNull(message = "Child passenger count is required")
    @Min(value = 0, message = "Child passenger count cannot be negative")
    @Column(nullable = false)
    private Integer childCount;

    @NotNull(message = "Reservation status is required")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING_OFFICE_PAYMENT;

    /** First activation timestamp of a Burmester Combo voucher; null until activated. */
    @Column
    private LocalDateTime comboActivatedAt;

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public int getTotalPassengers() {
        int adults = adultCount == null ? 0 : adultCount;
        int children = childCount == null ? 0 : childCount;
        return adults + children;
    }

    /**
     * Records the first activation of a Burmester Combo voucher. Activation can happen
     * at the boat dock or the wine cellar, whichever comes first; subsequent calls are
     * no-ops so the 48h window is never reset.
     */
    public void activateCombo(LocalDateTime activationTime) {
        if (tour == null || !tour.requiresComboActivation()) {
            throw new IllegalStateException("This tour does not support combo activation");
        }
        if (activationTime == null) {
            throw new IllegalArgumentException("Activation time is required");
        }
        if (comboActivatedAt == null) {
            comboActivatedAt = activationTime;
        }
    }

    public boolean isComboActive(LocalDateTime checkTime) {
        if (comboActivatedAt == null || checkTime == null) {
            return false;
        }
        Duration elapsed = Duration.between(comboActivatedAt, checkTime);
        return !elapsed.isNegative() && elapsed.compareTo(COMBO_VALIDITY_PERIOD) <= 0;
    }

    public LocalDateTime getComboExpiresAt() {
        return comboActivatedAt == null ? null : comboActivatedAt.plus(COMBO_VALIDITY_PERIOD);
    }

    public void changeStatus(ReservationStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status is required");
        }
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition reservation from " + status + " to " + newStatus);
        }
        this.status = newStatus;
    }
}
