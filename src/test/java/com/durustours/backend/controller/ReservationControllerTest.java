package com.durustours.backend.controller;

import com.durustours.backend.domain.ReservationStatus;
import com.durustours.backend.dto.ReservationRequestDTO;
import com.durustours.backend.dto.ReservationResponseDTO;
import com.durustours.backend.exception.GlobalExceptionHandler;
import com.durustours.backend.exception.InvalidReservationException;
import com.durustours.backend.exception.ResourceNotFoundException;
import com.durustours.backend.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@Import(GlobalExceptionHandler.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    private ReservationRequestDTO validRequest() {
        return new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 2, 1,
                "Maria Silva", "maria.silva@example.com", "+351912345678"
        );
    }

    private ReservationResponseDTO aResponse() {
        return ReservationResponseDTO.of(
                "DT-2026-X89B", "Douro Bridges Tour", LocalDate.now().plusDays(3), "10:30",
                2, 1, new BigDecimal("45.00"), ReservationStatus.PENDING_OFFICE_PAYMENT, false
        );
    }

    @Test
    void createReservationReturnsCreatedWithTheReservation() throws Exception {
        when(reservationService.createReservation(any(ReservationRequestDTO.class))).thenReturn(aResponse());

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.confirmationCode", is("DT-2026-X89B")))
                .andExpect(jsonPath("$.status", is("PENDING_OFFICE_PAYMENT")));
    }

    @Test
    void createReservationReturnsBadRequestOnValidationFailure() throws Exception {
        ReservationRequestDTO invalid = new ReservationRequestDTO(
                1L, LocalDate.now().plusDays(3), "10:30", 0, 1,
                "Maria Silva", "not-an-email", "+351912345678"
        );

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.adults").exists())
                .andExpect(jsonPath("$.fieldErrors.customerEmail").exists());
    }

    @Test
    void createReservationReturnsNotFoundWhenTourDoesNotExist() throws Exception {
        when(reservationService.createReservation(any(ReservationRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Tour not found: 1"));

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReservationReturnsBadRequestWhenTourIsNotBookable() throws Exception {
        when(reservationService.createReservation(any(ReservationRequestDTO.class)))
                .thenThrow(new InvalidReservationException("Tour 'X' is not currently bookable"));

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReservationReturnsOkWhenFound() throws Exception {
        when(reservationService.getReservationByConfirmationCode("DT-2026-X89B")).thenReturn(aResponse());

        mockMvc.perform(get("/api/v1/reservations/DT-2026-X89B"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmationCode", is("DT-2026-X89B")));
    }

    @Test
    void getReservationReturnsNotFoundWhenMissing() throws Exception {
        when(reservationService.getReservationByConfirmationCode("DT-2026-ZZZZ"))
                .thenThrow(new ResourceNotFoundException("Reservation not found: DT-2026-ZZZZ"));

        mockMvc.perform(get("/api/v1/reservations/DT-2026-ZZZZ"))
                .andExpect(status().isNotFound());
    }
}
