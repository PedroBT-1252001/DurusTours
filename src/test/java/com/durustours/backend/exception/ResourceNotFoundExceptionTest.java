package com.durustours.backend.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void isARuntimeExceptionCarryingTheGivenMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Tour not found: 42");

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo("Tour not found: 42");
    }
}
