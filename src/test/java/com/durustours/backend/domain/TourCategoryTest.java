package com.durustours.backend.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class TourCategoryTest {

    @Test
    void fiftyMinCruiseIsNotFullDay() {
        assertThat(TourCategory.FIFTY_MIN_CRUISE.isFullDay()).isFalse();
    }

    @Test
    void fullDayCruiseIsFullDay() {
        assertThat(TourCategory.FULL_DAY_CRUISE.isFullDay()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(TourCategory.class)
    void onlyFullDayCruiseIsFullDay(TourCategory category) {
        assertThat(category.isFullDay()).isEqualTo(category == TourCategory.FULL_DAY_CRUISE);
    }
}
