package com.durustours.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ReservationRequestDTO(

        @NotNull(message = "Tour ID is required")
        Long tourId,

        @NotNull(message = "Tour date is required")
        @FutureOrPresent(message = "Tour date cannot be in the past")
        LocalDate tourDate,

        @NotBlank(message = "Time slot is required")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Time slot must be in HH:mm format")
        String timeSlot,

        @Min(value = 1, message = "At least 1 adult is required")
        int adults,

        @Min(value = 0, message = "Children count cannot be negative")
        int children,

        @NotBlank(message = "Customer name is required")
        @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
        String customerName,

        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email must be valid")
        String customerEmail,

        @NotBlank(message = "Customer phone is required")
        @Pattern(regexp = "^\\+?[0-9\\s()-]{7,20}$", message = "Customer phone must be a valid phone number")
        String customerPhone
) {
}
