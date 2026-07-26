package com.durustours.backend.service;

import com.durustours.backend.domain.Customer;
import com.durustours.backend.domain.Reservation;
import com.durustours.backend.domain.ReservationStatus;
import com.durustours.backend.domain.Tour;
import com.durustours.backend.dto.ReservationRequestDTO;
import com.durustours.backend.dto.ReservationResponseDTO;
import com.durustours.backend.exception.InvalidReservationException;
import com.durustours.backend.exception.ResourceNotFoundException;
import com.durustours.backend.repository.ReservationRepository;
import com.durustours.backend.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final DateTimeFormatter TIME_SLOT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final int MAX_CONFIRMATION_CODE_ATTEMPTS = 5;

    private final ReservationRepository reservationRepository;
    private final TourRepository tourRepository;
    private final ConfirmationCodeGenerator confirmationCodeGenerator;

    public ReservationResponseDTO createReservation(ReservationRequestDTO request) {
        Tour tour = tourRepository.findById(request.tourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found: " + request.tourId()));

        if (!tour.isActive()) {
            throw new InvalidReservationException("Tour '" + tour.getTitle() + "' is not currently bookable");
        }

        LocalDateTime requestedDateTime = LocalDateTime.of(
                request.tourDate(), LocalTime.parse(request.timeSlot(), TIME_SLOT_FORMAT));

        Customer customer = Customer.builder()
                .fullName(request.customerName())
                .email(request.customerEmail())
                .phone(request.customerPhone())
                .build();

        Reservation reservation = Reservation.builder()
                .confirmationCode(generateUniqueConfirmationCode())
                .customer(customer)
                .tour(tour)
                .requestedDateTime(requestedDateTime)
                .adultCount(request.adults())
                .childCount(request.children())
                .status(ReservationStatus.PENDING_OFFICE_PAYMENT)
                .build();

        Reservation saved = reservationRepository.save(reservation);

        return toResponseDTO(saved);
    }

    public ReservationResponseDTO getReservationByConfirmationCode(String confirmationCode) {
        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + confirmationCode));

        return toResponseDTO(reservation);
    }

    private String generateUniqueConfirmationCode() {
        for (int attempt = 0; attempt < MAX_CONFIRMATION_CODE_ATTEMPTS; attempt++) {
            String candidate = confirmationCodeGenerator.generate();
            if (!reservationRepository.existsByConfirmationCode(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Unable to generate a unique confirmation code");
    }

    private ReservationResponseDTO toResponseDTO(Reservation reservation) {
        Tour tour = reservation.getTour();
        BigDecimal totalPrice = tour.getBasePrice()
                .multiply(BigDecimal.valueOf(reservation.getTotalPassengers()));

        return ReservationResponseDTO.of(
                reservation.getConfirmationCode(),
                tour.getTitle(),
                reservation.getRequestedDateTime().toLocalDate(),
                reservation.getRequestedDateTime().toLocalTime().format(TIME_SLOT_FORMAT),
                reservation.getAdultCount(),
                reservation.getChildCount(),
                totalPrice,
                reservation.getStatus(),
                tour.isCombo()
        );
    }
}
