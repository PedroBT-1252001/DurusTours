package com.durustours.backend.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Year;

@Component
public class ConfirmationCodeGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SUFFIX_LENGTH = 4;

    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public ConfirmationCodeGenerator() {
        this(Clock.systemDefaultZone());
    }

    public ConfirmationCodeGenerator(Clock clock) {
        this.clock = clock;
    }

    public String generate() {
        int year = Year.now(clock).getValue();
        StringBuilder suffix = new StringBuilder(SUFFIX_LENGTH);
        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            suffix.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return "DT-" + year + "-" + suffix;
    }
}
