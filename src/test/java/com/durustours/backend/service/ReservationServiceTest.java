package com.durustours.backend.service;

import com.durustours.backend.domain.Customer;
import com.durustours.backend.domain.Reservation;
import com.durustours.backend.domain.ReservationStatus;
import com.durustours.backend.domain.Tour;
import com.durustours.backend.domain.TourCategory;
import com.durustours.backend.dto.ReservationRequestDTO;
import com.durustours.backend.dto.ReservationResponseDTO;
import com.durustours.backend.exception.InvalidReservationException;
import com.durustours.backend.exception.ResourceNotFoundException;
import com.durustours.backend.repository.ReservationRepository;
import com.durustours.backend.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private ConfirmationCodeGenerator confirmationCodeGenerator;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository, tourRepository, confirmationCodeGenerator);
    }

    private Tour anActiveBridgesTour() {
        return Tour.builder()
                .id(1L)
                .category(TourCategory.FIFTY_MIN_CRUISE)
                .title("Douro Bridges Tour")
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("15.00"))
                .active(true)
                .build();
    }

    private ReservationRequestDTO aRequest() {
        return new ReservationRequestDTO(
                1L, LocalDate.of(2026, 8, 1), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );
    }

    @Test
    void createsAReservationForAnActiveTour() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(anActiveBridgesTour()));
        when(confirmationCodeGenerator.generate()).thenReturn("DT-2026-AAAA");
        when(reservationRepository.existsByConfirmationCode("DT-2026-AAAA")).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponseDTO response = reservationService.createReservation(aRequest());

        assertThat(response.confirmationCode()).isEqualTo("DT-2026-AAAA");
        assertThat(response.tourTitle()).isEqualTo("Douro Bridges Tour");
        assertThat(response.tourDate()).isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(response.timeSlot()).isEqualTo("10:30");
        assertThat(response.adults()).isEqualTo(2);
        assertThat(response.children()).isEqualTo(1);
        assertThat(response.totalPrice()).isEqualTo(new BigDecimal("45.00"));
        assertThat(response.status()).isEqualTo(ReservationStatus.PENDING_OFFICE_PAYMENT);
        assertThat(response.comboValidityNote()).isNull();
    }

    @Test
    void savesANewReservationDefaultingToPendingOfficePayment() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(anActiveBridgesTour()));
        when(confirmationCodeGenerator.generate()).thenReturn("DT-2026-AAAA");
        when(reservationRepository.existsByConfirmationCode("DT-2026-AAAA")).thenReturn(false);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        when(reservationRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        reservationService.createReservation(aRequest());

        assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.PENDING_OFFICE_PAYMENT);
        assertThat(captor.getValue().getCustomer().getEmail()).isEqualTo("maria.silva@example.com");
        assertThat(captor.getValue().getRequestedDateTime()).isEqualTo(LocalDateTime.of(2026, 8, 1, 10, 30));
    }

    @Test
    void populatesComboValidityNoteWhenTourIsACombo() {
        Tour comboTour = Tour.builder()
                .id(2L)
                .category(TourCategory.FIFTY_MIN_CRUISE)
                .title("Bridges Tour + Burmester Wine Cellar Combo")
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("25.00"))
                .active(true)
                .combo(true)
                .build();
        when(tourRepository.findById(2L)).thenReturn(Optional.of(comboTour));
        when(confirmationCodeGenerator.generate()).thenReturn("DT-2026-BBBB");
        when(reservationRepository.existsByConfirmationCode("DT-2026-BBBB")).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationRequestDTO request = new ReservationRequestDTO(
                2L, LocalDate.of(2026, 8, 1), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        ReservationResponseDTO response = reservationService.createReservation(request);

        assertThat(response.comboValidityNote()).contains("48 hours");
    }

    @Test
    void throwsWhenTheTourDoesNotExist() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        ReservationRequestDTO request = new ReservationRequestDTO(
                99L, LocalDate.of(2026, 8, 1), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );

        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void throwsWhenTheTourIsNotActive() {
        Tour inactiveTour = anActiveBridgesTour();
        inactiveTour.setActive(false);
        when(tourRepository.findById(1L)).thenReturn(Optional.of(inactiveTour));

        assertThatThrownBy(() -> reservationService.createReservation(aRequest()))
                .isInstanceOf(InvalidReservationException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void retriesConfirmationCodeGenerationOnCollision() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(anActiveBridgesTour()));
        when(confirmationCodeGenerator.generate()).thenReturn("DT-2026-DUPE", "DT-2026-UNIQ");
        when(reservationRepository.existsByConfirmationCode("DT-2026-DUPE")).thenReturn(true);
        when(reservationRepository.existsByConfirmationCode("DT-2026-UNIQ")).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponseDTO response = reservationService.createReservation(aRequest());

        assertThat(response.confirmationCode()).isEqualTo("DT-2026-UNIQ");
        verify(confirmationCodeGenerator, times(2)).generate();
    }

    @Test
    void givesUpAfterExhaustingConfirmationCodeAttempts() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(anActiveBridgesTour()));
        when(confirmationCodeGenerator.generate()).thenReturn("DT-2026-DUPE");
        when(reservationRepository.existsByConfirmationCode("DT-2026-DUPE")).thenReturn(true);

        assertThatThrownBy(() -> reservationService.createReservation(aRequest()))
                .isInstanceOf(IllegalStateException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void returnsAReservationByConfirmationCode() {
        Tour tour = anActiveBridgesTour();
        Reservation reservation = Reservation.builder()
                .confirmationCode("DT-2026-AAAA")
                .customer(Customer.builder()
                        .fullName("Maria Silva")
                        .email("maria.silva@example.com")
                        .phone("+351912345678")
                        .build())
                .tour(tour)
                .requestedDateTime(LocalDateTime.of(2026, 8, 1, 10, 30))
                .adultCount(2)
                .childCount(1)
                .status(ReservationStatus.PENDING_OFFICE_PAYMENT)
                .build();
        when(reservationRepository.findByConfirmationCode("DT-2026-AAAA")).thenReturn(Optional.of(reservation));

        ReservationResponseDTO response = reservationService.getReservationByConfirmationCode("DT-2026-AAAA");

        assertThat(response.confirmationCode()).isEqualTo("DT-2026-AAAA");
        assertThat(response.totalPrice()).isEqualTo(new BigDecimal("45.00"));
    }

    @Test
    void throwsWhenConfirmationCodeIsUnknown() {
        when(reservationRepository.findByConfirmationCode("DT-2026-ZZZZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getReservationByConfirmationCode("DT-2026-ZZZZ"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
