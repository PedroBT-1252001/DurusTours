package com.durustours.backend.controller;

import com.durustours.backend.domain.TourCategory;
import com.durustours.backend.dto.TourDTO;
import com.durustours.backend.exception.GlobalExceptionHandler;
import com.durustours.backend.exception.ResourceNotFoundException;
import com.durustours.backend.service.TourService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TourController.class)
@Import(GlobalExceptionHandler.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourService tourService;

    private TourDTO aTourDTO(Long id, String title) {
        return new TourDTO(id, TourCategory.FIFTY_MIN_CRUISE, title, "50-minute cruise", 50, "50 minutes",
                new BigDecimal("15.00"), false, true);
    }

    @Test
    void getAllToursReturnsOkWithTheActiveCatalog() throws Exception {
        when(tourService.getActiveTours()).thenReturn(List.of(aTourDTO(1L, "Douro Bridges Tour")));

        mockMvc.perform(get("/api/v1/tours"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Douro Bridges Tour")));
    }

    @Test
    void getTourByIdReturnsOkWhenFound() throws Exception {
        when(tourService.getTourById(1L)).thenReturn(aTourDTO(1L, "Douro Bridges Tour"));

        mockMvc.perform(get("/api/v1/tours/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Douro Bridges Tour")));
    }

    @Test
    void getTourByIdReturnsNotFoundWhenMissing() throws Exception {
        when(tourService.getTourById(99L)).thenThrow(new ResourceNotFoundException("Tour not found: 99"));

        mockMvc.perform(get("/api/v1/tours/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Tour not found: 99")));
    }
}
