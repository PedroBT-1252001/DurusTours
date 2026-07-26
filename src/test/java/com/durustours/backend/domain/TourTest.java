package com.durustours.backend.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TourTest {

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

    private Tour.TourBuilder validTourBuilder() {
        return Tour.builder()
                .category(TourCategory.FIFTY_MIN_CRUISE)
                .title("Douro Bridges Tour")
                .description("50-minute boat cruise highlighting Porto and Gaia's iconic 6 bridges")
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("15.00"));
    }

    @Test
    void createsAValidTourDefaultingToActiveAndNonCombo() {
        Tour tour = validTourBuilder().build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations).isEmpty();
        assertThat(tour.isActive()).isTrue();
        assertThat(tour.isCombo()).isFalse();
    }

    @Test
    void rejectsBlankTitle() {
        Tour tour = validTourBuilder().title(" ").build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("title");
    }

    @Test
    void rejectsNullCategory() {
        Tour tour = validTourBuilder().category(null).build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("category");
    }

    @Test
    void rejectsNonPositiveDurationMinutesWhenPresent() {
        Tour tour = validTourBuilder().durationMinutes(0).build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("durationMinutes");
    }

    @Test
    void allowsNullDurationMinutesForFullDayTours() {
        Tour tour = validTourBuilder()
                .category(TourCategory.FULL_DAY_CRUISE)
                .title("Porto to Regua Cruise")
                .durationMinutes(null)
                .durationLabel("Full day (approx. 10-12 hours)")
                .basePrice(new BigDecimal("95.00"))
                .build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations).isEmpty();
    }

    @Test
    void rejectsBlankDurationLabel() {
        Tour tour = validTourBuilder().durationLabel(" ").build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("durationLabel");
    }

    @Test
    void rejectsNegativeBasePrice() {
        Tour tour = validTourBuilder().basePrice(new BigDecimal("-1.00")).build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("basePrice");
    }

    @Test
    void nonComboTourDoesNotRequireComboActivation() {
        Tour tour = validTourBuilder().combo(false).build();

        assertThat(tour.requiresComboActivation()).isFalse();
    }

    @Test
    void comboTourRequiresComboActivation() {
        Tour tour = validTourBuilder()
                .title("Bridges Tour + Burmester Wine Cellar Combo")
                .combo(true)
                .basePrice(new BigDecimal("25.00"))
                .build();

        assertThat(tour.requiresComboActivation()).isTrue();
        assertThat(tour.isCombo()).isTrue();
    }
}
