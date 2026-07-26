package com.durustours.backend.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationStatusTest {

    @ParameterizedTest
    @CsvSource({
            "PENDING_OFFICE_PAYMENT, PAID_ONLINE, true",
            "PENDING_OFFICE_PAYMENT, CONFIRMED, true",
            "PENDING_OFFICE_PAYMENT, EXPIRED, true",
            "PENDING_OFFICE_PAYMENT, CANCELLED, true",
            "PAID_ONLINE, CONFIRMED, true",
            "PAID_ONLINE, CANCELLED, true",
            "PAID_ONLINE, PENDING_OFFICE_PAYMENT, false",
            "PAID_ONLINE, EXPIRED, false",
            "CONFIRMED, CANCELLED, true",
            "CONFIRMED, PAID_ONLINE, false",
            "CONFIRMED, PENDING_OFFICE_PAYMENT, false",
            "EXPIRED, PENDING_OFFICE_PAYMENT, false",
            "EXPIRED, CONFIRMED, false",
            "CANCELLED, PENDING_OFFICE_PAYMENT, false",
            "CANCELLED, CONFIRMED, false",
    })
    void enforcesAllowedStateTransitions(ReservationStatus from, ReservationStatus to, boolean expectedAllowed) {
        assertThat(from.canTransitionTo(to)).isEqualTo(expectedAllowed);
    }

    @Test
    void aStatusCannotTransitionToItself() {
        for (ReservationStatus status : ReservationStatus.values()) {
            assertThat(status.canTransitionTo(status)).isFalse();
        }
    }

    @Test
    void canTransitionToNullIsAlwaysFalse() {
        for (ReservationStatus status : ReservationStatus.values()) {
            assertThat(status.canTransitionTo(null)).isFalse();
        }
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.class, names = {"EXPIRED", "CANCELLED"})
    void expiredAndCancelledAreTerminalStates(ReservationStatus status) {
        assertThat(status.isTerminal()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.class, names = {"PENDING_OFFICE_PAYMENT", "PAID_ONLINE", "CONFIRMED"})
    void nonTerminalStatesAreNotTerminal(ReservationStatus status) {
        assertThat(status.isTerminal()).isFalse();
    }

    @Test
    void defaultReservationStatusIsPendingOfficePayment() {
        Reservation reservation = Reservation.builder().build();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING_OFFICE_PAYMENT);
    }
}
