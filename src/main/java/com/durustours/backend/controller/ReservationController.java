package com.durustours.backend.controller;

import com.durustours.backend.dto.ReservationRequestDTO;
import com.durustours.backend.dto.ReservationResponseDTO;
import com.durustours.backend.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDTO createReservation(@Valid @RequestBody ReservationRequestDTO request) {
        return reservationService.createReservation(request);
    }

    @GetMapping("/{confirmationCode}")
    public ReservationResponseDTO getReservation(@PathVariable String confirmationCode) {
        return reservationService.getReservationByConfirmationCode(confirmationCode);
    }
}
