package com.durustours.backend.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Reservation lifecycle state machine. Reservations are created in
 * PENDING_OFFICE_PAYMENT since payment currently happens at the physical office;
 * PAID_ONLINE/CONFIRMED are future-proofing for online payment support.
 */
public enum ReservationStatus {

    PENDING_OFFICE_PAYMENT,
    PAID_ONLINE,
    CONFIRMED,
    EXPIRED,
    CANCELLED;

    private static final Map<ReservationStatus, Set<ReservationStatus>> ALLOWED_TRANSITIONS = Map.of(
            PENDING_OFFICE_PAYMENT, EnumSet.of(PAID_ONLINE, CONFIRMED, EXPIRED, CANCELLED),
            PAID_ONLINE, EnumSet.of(CONFIRMED, CANCELLED),
            CONFIRMED, EnumSet.of(CANCELLED),
            EXPIRED, EnumSet.noneOf(ReservationStatus.class),
            CANCELLED, EnumSet.noneOf(ReservationStatus.class)
    );

    public boolean canTransitionTo(ReservationStatus target) {
        if (target == null) {
            return false;
        }
        return ALLOWED_TRANSITIONS.get(this).contains(target);
    }

    public boolean isTerminal() {
        return this == EXPIRED || this == CANCELLED;
    }
}
