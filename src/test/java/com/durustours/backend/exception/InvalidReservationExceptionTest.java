package com.durustours.backend.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidReservationExceptionTest {

    @Test
    void isARuntimeExceptionCarryingTheGivenMessage() {
        InvalidReservationException exception = new InvalidReservationException("Tour is not currently bookable");

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo("Tour is not currently bookable");
    }
}
