package com.durustours.backend.dto;

import com.durustours.backend.domain.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationResponseDTO(
        String confirmationCode,
        String tourTitle,
        LocalDate tourDate,
        String timeSlot,
        int adults,
        int children,
        BigDecimal totalPrice,
        ReservationStatus status,
        String pickupInstructions,
        String comboValidityNote
) {

    private static final String PICKUP_INSTRUCTIONS =
            "Please present this code at our Porto dock office 20 minutes prior to departure to pay and collect your tickets.";

    private static final String COMBO_VALIDITY_NOTE =
            "This voucher is valid for 48 hours from first activation, either at the boat dock or the Burmester wine cellar.";

    public static ReservationResponseDTO of(
            String confirmationCode,
            String tourTitle,
            LocalDate tourDate,
            String timeSlot,
            int adults,
            int children,
            BigDecimal totalPrice,
            ReservationStatus status,
            boolean isCombo
    ) {
        return new ReservationResponseDTO(
                confirmationCode,
                tourTitle,
                tourDate,
                timeSlot,
                adults,
                children,
                totalPrice,
                status,
                PICKUP_INSTRUCTIONS,
                isCombo ? COMBO_VALIDITY_NOTE : null
        );
    }
}
