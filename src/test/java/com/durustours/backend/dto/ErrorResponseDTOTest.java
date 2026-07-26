package com.durustours.backend.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDTOTest {

    @Test
    void carriesAllProvidedFields() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 8, 1, 10, 0);
        Map<String, String> fieldErrors = Map.of("adults", "At least 1 adult is required");

        ErrorResponseDTO error = new ErrorResponseDTO(timestamp, 400, "Bad Request", "Validation failed", fieldErrors);

        assertThat(error.timestamp()).isEqualTo(timestamp);
        assertThat(error.status()).isEqualTo(400);
        assertThat(error.error()).isEqualTo("Bad Request");
        assertThat(error.message()).isEqualTo("Validation failed");
        assertThat(error.fieldErrors()).isEqualTo(fieldErrors);
    }
}
