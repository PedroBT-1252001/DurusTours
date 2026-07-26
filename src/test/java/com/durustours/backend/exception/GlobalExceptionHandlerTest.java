package com.durustours.backend.exception;

import com.durustours.backend.dto.ErrorResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsResourceNotFoundExceptionTo404() {
        ResponseEntity<ErrorResponseDTO> response =
                handler.handleNotFound(new ResourceNotFoundException("Tour not found: 42"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Tour not found: 42");
    }

    @Test
    void mapsInvalidReservationExceptionTo400() {
        ResponseEntity<ErrorResponseDTO> response =
                handler.handleInvalidReservation(new InvalidReservationException("Tour is not bookable"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Tour is not bookable");
    }

    @Test
    void mapsValidationErrorsToFieldErrorsMap() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "adults", "At least 1 adult is required"));
        bindingResult.addError(new FieldError("request", "customerEmail", "Customer email must be valid"));

        MethodParameter methodParameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("mapsValidationErrorsToFieldErrorsMap"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().fieldErrors())
                .containsEntry("adults", "At least 1 adult is required")
                .containsEntry("customerEmail", "Customer email must be valid");
    }

    @Test
    void mapsUnexpectedExceptionsTo500WithoutLeakingDetails() {
        ResponseEntity<ErrorResponseDTO> response = handler.handleUnexpected(new RuntimeException("some internal detail"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).doesNotContain("some internal detail");
    }
}
