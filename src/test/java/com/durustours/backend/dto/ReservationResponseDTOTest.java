package com.durustours.backend.dto;

import com.durustours.backend.domain.ReservationStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationResponseDTOTest {

    @Test
    void mapsAllFieldsForARegularTourReservation() {
        LocalDate tourDate = LocalDate.of(2026, 8, 1);

        ReservationResponseDTO response = ReservationResponseDTO.of(
                "DT-2026-X89B",
                "Douro Bridges Tour",
                tourDate,
                "10:30",
                2,
                1,
                new BigDecimal("37.50"),
                ReservationStatus.PENDING_OFFICE_PAYMENT,
                false
        );

        assertThat(response.confirmationCode()).isEqualTo("DT-2026-X89B");
        assertThat(response.tourTitle()).isEqualTo("Douro Bridges Tour");
        assertThat(response.tourDate()).isEqualTo(tourDate);
        assertThat(response.timeSlot()).isEqualTo("10:30");
        assertThat(response.adults()).isEqualTo(2);
        assertThat(response.children()).isEqualTo(1);
        assertThat(response.totalPrice()).isEqualTo(new BigDecimal("37.50"));
        assertThat(response.status()).isEqualTo(ReservationStatus.PENDING_OFFICE_PAYMENT);
    }

    @Test
    void populatesPickupInstructionsForEveryReservation() {
        ReservationResponseDTO response = ReservationResponseDTO.of(
                "DT-2026-X89B", "Douro Bridges Tour", LocalDate.now().plusDays(3), "10:30",
                2, 1, new BigDecimal("37.50"), ReservationStatus.PENDING_OFFICE_PAYMENT, false
        );

        assertThat(response.pickupInstructions())
                .contains("Porto dock office")
                .contains("20 minutes prior");
    }

    @Test
    void comboValidityNoteIsPresentForComboTours() {
        ReservationResponseDTO response = ReservationResponseDTO.of(
                "DT-2026-X89B", "Bridges Tour + Burmester Wine Cellar Combo", LocalDate.now().plusDays(3), "10:30",
                2, 1, new BigDecimal("62.50"), ReservationStatus.PENDING_OFFICE_PAYMENT, true
        );

        assertThat(response.comboValidityNote())
                .isNotBlank()
                .contains("48 hours");
    }

    @Test
    void comboValidityNoteIsAbsentForRegularTours() {
        ReservationResponseDTO response = ReservationResponseDTO.of(
                "DT-2026-X89B", "Douro Bridges Tour", LocalDate.now().plusDays(3), "10:30",
                2, 1, new BigDecimal("37.50"), ReservationStatus.PENDING_OFFICE_PAYMENT, false
        );

        assertThat(response.comboValidityNote()).isNull();
    }
}
