package com.durustours.backend.dto;

import com.durustours.backend.domain.Tour;
import com.durustours.backend.domain.TourCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TourDTOTest {

    @Test
    void mapsAllPropertiesFromTheTourEntity() {
        Tour tour = Tour.builder()
                .id(1L)
                .category(TourCategory.FIFTY_MIN_CRUISE)
                .title("Douro Bridges Tour")
                .description("50-minute boat cruise highlighting Porto and Gaia's iconic 6 bridges.")
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("15.00"))
                .combo(false)
                .active(true)
                .build();

        TourDTO dto = TourDTO.from(tour);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.category()).isEqualTo(TourCategory.FIFTY_MIN_CRUISE);
        assertThat(dto.title()).isEqualTo("Douro Bridges Tour");
        assertThat(dto.description()).isEqualTo(tour.getDescription());
        assertThat(dto.durationMinutes()).isEqualTo(50);
        assertThat(dto.durationLabel()).isEqualTo("50 minutes");
        assertThat(dto.basePrice()).isEqualTo(new BigDecimal("15.00"));
        assertThat(dto.combo()).isFalse();
        assertThat(dto.active()).isTrue();
    }

    @Test
    void mapsNullDurationMinutesForFullDayTours() {
        Tour tour = Tour.builder()
                .id(3L)
                .category(TourCategory.FULL_DAY_CRUISE)
                .title("Porto to Regua Cruise")
                .durationMinutes(null)
                .durationLabel("Full day (approx. 10-12 hours)")
                .basePrice(new BigDecimal("95.00"))
                .build();

        TourDTO dto = TourDTO.from(tour);

        assertThat(dto.durationMinutes()).isNull();
    }
}
