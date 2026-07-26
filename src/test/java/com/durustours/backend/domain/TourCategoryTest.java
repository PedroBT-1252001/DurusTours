package com.durustours.backend.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class TourCategoryTest {

    @Test
    void bridgesTourIsA50MinuteRiverCruiseWithoutComboActivation() {
        assertThat(TourCategory.BRIDGES_TOUR.getCruiseType()).isEqualTo(TourCategory.CruiseType.RIVER_CRUISE);
        assertThat(TourCategory.BRIDGES_TOUR.getStandardDurationMinutes()).isEqualTo(50);
        assertThat(TourCategory.BRIDGES_TOUR.requiresComboActivation()).isFalse();
        assertThat(TourCategory.BRIDGES_TOUR.isFullDay()).isFalse();
    }

    @Test
    void burmesterComboIsA50MinuteRiverCruiseRequiringComboActivation() {
        assertThat(TourCategory.BURMESTER_COMBO.getCruiseType()).isEqualTo(TourCategory.CruiseType.RIVER_CRUISE);
        assertThat(TourCategory.BURMESTER_COMBO.getStandardDurationMinutes()).isEqualTo(50);
        assertThat(TourCategory.BURMESTER_COMBO.requiresComboActivation()).isTrue();
        assertThat(TourCategory.BURMESTER_COMBO.isFullDay()).isFalse();
    }

    @Test
    void reguaFullDayIsAFullDayCruiseWithoutComboActivation() {
        assertThat(TourCategory.REGUA_FULL_DAY.getCruiseType()).isEqualTo(TourCategory.CruiseType.FULL_DAY_CRUISE);
        assertThat(TourCategory.REGUA_FULL_DAY.getStandardDurationMinutes()).isNull();
        assertThat(TourCategory.REGUA_FULL_DAY.requiresComboActivation()).isFalse();
        assertThat(TourCategory.REGUA_FULL_DAY.isFullDay()).isTrue();
    }

    @Test
    void pinhaoFullDayIsAFullDayCruiseWithoutComboActivation() {
        assertThat(TourCategory.PINHAO_FULL_DAY.getCruiseType()).isEqualTo(TourCategory.CruiseType.FULL_DAY_CRUISE);
        assertThat(TourCategory.PINHAO_FULL_DAY.requiresComboActivation()).isFalse();
        assertThat(TourCategory.PINHAO_FULL_DAY.isFullDay()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(TourCategory.class)
    void onlyBurmesterComboRequiresComboActivation(TourCategory category) {
        boolean expected = category == TourCategory.BURMESTER_COMBO;
        assertThat(category.requiresComboActivation()).isEqualTo(expected);
    }
}
