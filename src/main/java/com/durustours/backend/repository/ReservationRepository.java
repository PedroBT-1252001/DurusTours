package com.durustours.backend.repository;

import com.durustours.backend.domain.Reservation;
import com.durustours.backend.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByConfirmationCode(String confirmationCode);

    boolean existsByConfirmationCode(String confirmationCode);

    List<Reservation> findByTourIdAndRequestedDateTimeAndStatusNot(
            Long tourId, LocalDateTime requestedDateTime, ReservationStatus excludedStatus);
}
