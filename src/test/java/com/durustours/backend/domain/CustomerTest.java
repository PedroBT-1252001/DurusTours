package com.durustours.backend.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

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

    @Test
    void createsAValidCustomer() {
        Customer customer = Customer.builder()
                .fullName("Maria Silva")
                .email("maria.silva@example.com")
                .phone("+351912345678")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations).isEmpty();
        assertThat(customer.getFullName()).isEqualTo("Maria Silva");
        assertThat(customer.getEmail()).isEqualTo("maria.silva@example.com");
        assertThat(customer.getPhone()).isEqualTo("+351912345678");
    }

    @Test
    void rejectsBlankFullName() {
        Customer customer = Customer.builder()
                .fullName(" ")
                .email("maria.silva@example.com")
                .phone("+351912345678")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("fullName");
    }

    @Test
    void rejectsInvalidEmail() {
        Customer customer = Customer.builder()
                .fullName("Maria Silva")
                .email("not-an-email")
                .phone("+351912345678")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("email");
    }

    @Test
    void rejectsBlankPhone() {
        Customer customer = Customer.builder()
                .fullName("Maria Silva")
                .email("maria.silva@example.com")
                .phone("")
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("phone");
    }
}
