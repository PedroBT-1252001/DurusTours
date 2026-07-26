package com.durustours.backend.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

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

    private Customer aCustomer() {
        return Customer.builder()
                .fullName("Joao Pereira")
                .email("joao.pereira@example.com")
                .phone("+351913456789")
                .build();
    }

    private Tour aBridgesTour() {
        return Tour.builder()
                .category(TourCategory.FIFTY_MIN_CRUISE)
                .title("6 Bridges Cruise")
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("15.00"))
                .build();
    }

    private Tour aBurmesterComboTour() {
        return Tour.builder()
                .category(TourCategory.FIFTY_MIN_CRUISE)
                .title("Cruise + Burmester Cellar")
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("25.00"))
                .combo(true)
                .build();
    }

    private Reservation.ReservationBuilder validReservationBuilder() {
        return Reservation.builder()
                .confirmationCode("DT-2026-0001")
                .customer(aCustomer())
                .tour(aBridgesTour())
                .requestedDateTime(LocalDateTime.of(2026, 8, 1, 10, 0))
                .adultCount(2)
                .childCount(1);
    }

    @Test
    void createsAValidReservationDefaultingToPendingOfficePayment() {
        Reservation reservation = validReservationBuilder().build();

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        assertThat(violations).isEmpty();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING_OFFICE_PAYMENT);
        assertThat(reservation.getCreatedAt()).isNotNull();
    }

    @Test
    void rejectsBlankConfirmationCode() {
        Reservation reservation = validReservationBuilder().confirmationCode(" ").build();

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("confirmationCode");
    }

    @Test
    void rejectsMissingCustomer() {
        Reservation reservation = validReservationBuilder().customer(null).build();

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("customer");
    }

    @Test
    void rejectsMissingTour() {
        Reservation reservation = validReservationBuilder().tour(null).build();

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("tour");
    }

    @Test
    void rejectsMissingRequestedDateTime() {
        Reservation reservation = validReservationBuilder().requestedDateTime(null).build();

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("requestedDateTime");
    }

    @Test
    void rejectsNegativeAdultCount() {
        Reservation reservation = validReservationBuilder().adultCount(-1).build();

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("adultCount");
    }

    @Test
    void rejectsNegativeChildCount() {
        Reservation reservation = validReservationBuilder().childCount(-1).build();

        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("childCount");
    }

    @Test
    void totalPassengersSumsAdultsAndChildren() {
        Reservation reservation = validReservationBuilder().adultCount(2).childCount(3).build();

        assertThat(reservation.getTotalPassengers()).isEqualTo(5);
    }

    // --- 48-hour Burmester Combo activation logic ---

    @Test
    void activatingAComboTourSetsTheFirstActivationTimestamp() {
        Reservation reservation = validReservationBuilder().tour(aBurmesterComboTour()).build();
        LocalDateTime dockActivation = LocalDateTime.of(2026, 8, 1, 10, 0);

        reservation.activateCombo(dockActivation);

        assertThat(reservation.getComboActivatedAt()).isEqualTo(dockActivation);
    }

    @Test
    void secondActivationDoesNotResetTheOriginalTimestamp() {
        Reservation reservation = validReservationBuilder().tour(aBurmesterComboTour()).build();
        LocalDateTime dockActivation = LocalDateTime.of(2026, 8, 1, 10, 0);
        LocalDateTime cellarActivationLater = LocalDateTime.of(2026, 8, 1, 14, 0);

        reservation.activateCombo(dockActivation);
        reservation.activateCombo(cellarActivationLater);

        assertThat(reservation.getComboActivatedAt()).isEqualTo(dockActivation);
    }

    @Test
    void activatingAComboOnATourThatDoesNotSupportItThrows() {
        Reservation reservation = validReservationBuilder().tour(aBridgesTour()).build();

        assertThatThrownBy(() -> reservation.activateCombo(LocalDateTime.now()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void activatingWithoutATimestampThrows() {
        Reservation reservation = validReservationBuilder().tour(aBurmesterComboTour()).build();

        assertThatThrownBy(() -> reservation.activateCombo(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void comboIsActiveWithin48HoursOfActivation() {
        Reservation reservation = validReservationBuilder().tour(aBurmesterComboTour()).build();
        LocalDateTime activation = LocalDateTime.of(2026, 8, 1, 10, 0);
        reservation.activateCombo(activation);

        assertThat(reservation.isComboActive(activation)).isTrue();
        assertThat(reservation.isComboActive(activation.plusHours(47).plusMinutes(59))).isTrue();
        assertThat(reservation.isComboActive(activation.plusHours(48))).isTrue();
    }

    @Test
    void comboIsNoLongerActiveAfter48Hours() {
        Reservation reservation = validReservationBuilder().tour(aBurmesterComboTour()).build();
        LocalDateTime activation = LocalDateTime.of(2026, 8, 1, 10, 0);
        reservation.activateCombo(activation);

        assertThat(reservation.isComboActive(activation.plusHours(48).plusMinutes(1))).isFalse();
    }

    @Test
    void comboIsNotActiveBeforeActivation() {
        Reservation reservation = validReservationBuilder().tour(aBurmesterComboTour()).build();

        assertThat(reservation.isComboActive(LocalDateTime.now())).isFalse();
    }

    @Test
    void comboExpiresAt48HoursAfterActivation() {
        Reservation reservation = validReservationBuilder().tour(aBurmesterComboTour()).build();
        LocalDateTime activation = LocalDateTime.of(2026, 8, 1, 10, 0);
        reservation.activateCombo(activation);

        assertThat(reservation.getComboExpiresAt()).isEqualTo(activation.plusHours(48));
    }

    @Test
    void comboExpiresAtIsNullWhenNeverActivated() {
        Reservation reservation = validReservationBuilder().tour(aBurmesterComboTour()).build();

        assertThat(reservation.getComboExpiresAt()).isNull();
    }

    // --- Reservation status transitions ---

    @Test
    void changeStatusAllowsAValidTransition() {
        Reservation reservation = validReservationBuilder().build();

        reservation.changeStatus(ReservationStatus.PAID_ONLINE);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PAID_ONLINE);
    }

    @Test
    void changeStatusRejectsAnInvalidTransition() {
        Reservation reservation = validReservationBuilder().status(ReservationStatus.CANCELLED).build();

        assertThatThrownBy(() -> reservation.changeStatus(ReservationStatus.CONFIRMED))
                .isInstanceOf(IllegalStateException.class);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void changeStatusRejectsNull() {
        Reservation reservation = validReservationBuilder().build();

        assertThatThrownBy(() -> reservation.changeStatus(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
