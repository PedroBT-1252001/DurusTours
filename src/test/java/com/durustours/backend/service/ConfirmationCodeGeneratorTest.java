package com.durustours.backend.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmationCodeGeneratorTest {

    @Test
    void generatesACodeMatchingTheExpectedFormat() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-08-01T10:00:00Z"), ZoneOffset.UTC);
        ConfirmationCodeGenerator generator = new ConfirmationCodeGenerator(fixedClock);

        String code = generator.generate();

        assertThat(code).matches("^DT-2026-[A-Z0-9]{4}$");
    }

    @Test
    void usesTheYearFromTheProvidedClock() {
        Clock fixedClock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
        ConfirmationCodeGenerator generator = new ConfirmationCodeGenerator(fixedClock);

        assertThat(generator.generate()).startsWith("DT-2030-");
    }

    @Test
    void generatesDifferentCodesAcrossCalls() {
        ConfirmationCodeGenerator generator = new ConfirmationCodeGenerator();

        String first = generator.generate();
        String second = generator.generate();

        assertThat(first).isNotEqualTo(second);
    }
}
