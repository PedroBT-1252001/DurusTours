package com.durustours.backend.repository;

import com.durustours.backend.domain.Tour;
import com.durustours.backend.domain.TourCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class TourRepositoryTest {

    @Autowired
    private TourRepository tourRepository;

    private Tour aTour(TourCategory category, String title, boolean active) {
        return Tour.builder()
                .category(category)
                .title(title)
                .durationMinutes(50)
                .durationLabel("50 minutes")
                .basePrice(new BigDecimal("15.00"))
                .active(active)
                .build();
    }

    @Test
    void savesAndFindsATourById() {
        Tour saved = tourRepository.save(aTour(TourCategory.FIFTY_MIN_CRUISE, "Douro Bridges Tour", true));

        assertThat(tourRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Tour::getTitle)
                .isEqualTo("Douro Bridges Tour");
    }

    @Test
    void findsOnlyActiveTours() {
        tourRepository.save(aTour(TourCategory.FIFTY_MIN_CRUISE, "Active Tour", true));
        tourRepository.save(aTour(TourCategory.FIFTY_MIN_CRUISE, "Retired Tour", false));

        List<Tour> activeTours = tourRepository.findByActiveTrue();

        assertThat(activeTours)
                .extracting(Tour::getTitle)
                .containsExactly("Active Tour");
    }

    @Test
    void findsToursByCategory() {
        tourRepository.save(aTour(TourCategory.FIFTY_MIN_CRUISE, "50 Min Tour", true));
        tourRepository.save(aTour(TourCategory.FULL_DAY_CRUISE, "Full Day Tour", true));

        List<Tour> fullDayTours = tourRepository.findByCategory(TourCategory.FULL_DAY_CRUISE);

        assertThat(fullDayTours)
                .extracting(Tour::getTitle)
                .containsExactly("Full Day Tour");
    }
}
