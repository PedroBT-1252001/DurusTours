package com.durustours.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationRequestDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    private ReservationRequestDTO validRequest() {
        return new ReservationRequestDTO(
                1L,
                LocalDate.now().plusDays(3),
                "10:30",
                2,
                1,
                "Maria Silva",
                "maria.silva@example.com",
                "+351912345678"
        );
    }

    @Test
    void acceptsAFullyValidPayload() {
        Set<ConstraintViolation<ReservationRequestDTO>> violations = validator.validate(validRequest());

        assertThat(violations).isEmpty();
    }

    @Test
    void acceptsAReservationForToday() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now(), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsMissingTourId() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                null, LocalDate.now().plusDays(3), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("tourId");
    }

    @Test
    void rejectsMissingTourDate() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, null, "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("tourDate");
    }

    @Test
    void rejectsAPastTourDate() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().minusDays(1), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("tourDate");
    }

    @Test
    void rejectsBlankTimeSlot() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), " ", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("timeSlot");
    }

    @Test
    void rejectsATimeSlotNotMatchingHhMmFormat() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30 AM", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("timeSlot");
    }

    @Test
    void rejectsZeroAdults() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 0, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("adults");
    }

    @Test
    void rejectsNegativeChildren() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, -1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("children");
    }

    @Test
    void rejectsBlankCustomerName() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, 1,
                " ", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("customerName");
    }

    @Test
    void rejectsATooShortCustomerName() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, 1,
                "M", "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("customerName");
    }

    @Test
    void rejectsATooLongCustomerName() {
        String longName = "A".repeat(101);
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, 1,
                longName, "maria.silva@example.com", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("customerName");
    }

    @Test
    void rejectsBlankCustomerEmail() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, 1,
                "Maria Silva", " ", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("customerEmail");
    }

    @Test
    void rejectsAnInvalidCustomerEmail() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, 1,
                "Maria Silva", "not-an-email", "+351912345678"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("customerEmail");
    }

    @Test
    void rejectsBlankCustomerPhone() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", " "
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("customerPhone");
    }

    @Test
    void rejectsAnInvalidCustomerPhone() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "abc123"
        );

        assertThat(validator.validate(request))
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("customerPhone");
    }
}
