package com.durustours.backend.repository;

import com.durustours.backend.domain.Customer;
import com.durustours.backend.domain.Reservation;
import com.durustours.backend.domain.ReservationStatus;
import com.durustours.backend.domain.Tour;
import com.durustours.backend.domain.TourCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TourRepository tourRepository;

    private Tour aTour() {
        return tourRepository.save(Tour.builder()
                .category(TourCategory.FIFTY_MIN_CRUISE)
                .title("Douro Bridges Tour")
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("15.00"))
                .build());
    }

    private Customer aCustomer() {
        return Customer.builder()
                .fullName("Maria Silva")
                .email("maria.silva@example.com")
                .phone("+351912345678")
                .build();
    }

    private Reservation aReservation(Tour tour, String confirmationCode, LocalDateTime slot, ReservationStatus status) {
        return Reservation.builder()
                .confirmationCode(confirmationCode)
                .customer(aCustomer())
                .tour(tour)
                .requestedDateTime(slot)
                .adultCount(2)
                .childCount(0)
                .status(status)
                .build();
    }

    @Test
    void findsAReservationByConfirmationCode() {
        Tour tour = aTour();
        reservationRepository.save(aReservation(
                tour, "DT-2026-AAAA", LocalDateTime.of(2026, 8, 1, 10, 0), ReservationStatus.PENDING_OFFICE_PAYMENT));

        Optional<Reservation> found = reservationRepository.findByConfirmationCode("DT-2026-AAAA");

        assertThat(found).isPresent();
        assertThat(found.get().getConfirmationCode()).isEqualTo("DT-2026-AAAA");
    }

    @Test
    void returnsEmptyWhenConfirmationCodeIsUnknown() {
        assertThat(reservationRepository.findByConfirmationCode("DT-2026-ZZZZ")).isEmpty();
    }

    @Test
    void existsByConfirmationCodeReflectsWhatWasPersisted() {
        Tour tour = aTour();
        reservationRepository.save(aReservation(
                tour, "DT-2026-BBBB", LocalDateTime.of(2026, 8, 1, 10, 0), ReservationStatus.PENDING_OFFICE_PAYMENT));

        assertThat(reservationRepository.existsByConfirmationCode("DT-2026-BBBB")).isTrue();
        assertThat(reservationRepository.existsByConfirmationCode("DT-2026-CCCC")).isFalse();
    }

    @Test
    void findsNonCancelledReservationsForATourAndSlot() {
        Tour tour = aTour();
        LocalDateTime slot = LocalDateTime.of(2026, 8, 1, 10, 0);
        reservationRepository.save(aReservation(tour, "DT-2026-DDDD", slot, ReservationStatus.PENDING_OFFICE_PAYMENT));
        reservationRepository.save(aReservation(tour, "DT-2026-EEEE", slot, ReservationStatus.CANCELLED));

        List<Reservation> active = reservationRepository.findByTourIdAndRequestedDateTimeAndStatusNot(
                tour.getId(), slot, ReservationStatus.CANCELLED);

        assertThat(active)
                .extracting(Reservation::getConfirmationCode)
                .containsExactly("DT-2026-DDDD");
    }
}
