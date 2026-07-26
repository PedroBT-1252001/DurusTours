package com.durustours.backend.service;

import com.durustours.backend.domain.Tour;
import com.durustours.backend.domain.TourCategory;
import com.durustours.backend.dto.TourDTO;
import com.durustours.backend.exception.ResourceNotFoundException;
import com.durustours.backend.repository.TourRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourService tourService;

    private Tour aTour(Long id, String title) {
        return Tour.builder()
                .id(id)
                .category(TourCategory.FIFTY_MIN_CRUISE)
                .title(title)
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("15.00"))
                .build();
    }

    @Test
    void returnsOnlyActiveToursAsDTOs() {
        when(tourRepository.findByActiveTrue()).thenReturn(List.of(aTour(1L, "Douro Bridges Tour")));

        List<TourDTO> tours = tourService.getActiveTours();

        assertThat(tours).hasSize(1);
        assertThat(tours.get(0).title()).isEqualTo("Douro Bridges Tour");
    }

    @Test
    void returnsATourDTOById() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(aTour(1L, "Douro Bridges Tour")));

        TourDTO tour = tourService.getTourById(1L);

        assertThat(tour.id()).isEqualTo(1L);
        assertThat(tour.title()).isEqualTo("Douro Bridges Tour");
    }

    @Test
    void throwsWhenTourIdDoesNotExist() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourService.getTourById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
