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
                .category(TourCategory.BRIDGES_TOUR)
                .name("6 Bridges Cruise")
                .description("A 50-minute cruise along the 6 bridges of Porto/Gaia")
                .durationMinutes(50)
                .priceAdult(new BigDecimal("15.00"))
                .priceChild(new BigDecimal("7.50"));
    }

    @Test
    void createsAValidTourDefaultingToActive() {
        Tour tour = validTourBuilder().build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations).isEmpty();
        assertThat(tour.isActive()).isTrue();
    }

    @Test
    void rejectsBlankName() {
        Tour tour = validTourBuilder().name(" ").build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("name");
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
    void rejectsNonPositiveDuration() {
        Tour tour = validTourBuilder().durationMinutes(0).build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("durationMinutes");
    }

    @Test
    void rejectsNegativeAdultPrice() {
        Tour tour = validTourBuilder().priceAdult(new BigDecimal("-1.00")).build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("priceAdult");
    }

    @Test
    void rejectsNegativeChildPrice() {
        Tour tour = validTourBuilder().priceChild(new BigDecimal("-1.00")).build();

        Set<ConstraintViolation<Tour>> violations = validator.validate(tour);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("priceChild");
    }

    @Test
    void bridgesTourDoesNotRequireComboActivation() {
        Tour tour = validTourBuilder().category(TourCategory.BRIDGES_TOUR).build();

        assertThat(tour.requiresComboActivation()).isFalse();
    }

    @Test
    void burmesterComboTourRequiresComboActivation() {
        Tour tour = validTourBuilder()
                .category(TourCategory.BURMESTER_COMBO)
                .name("Cruise + Burmester Cellar")
                .build();

        assertThat(tour.requiresComboActivation()).isTrue();
    }
}
