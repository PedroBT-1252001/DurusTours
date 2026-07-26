package com.durustours.backend.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TourCategoryConverterTest {

    private final TourCategoryConverter converter = new TourCategoryConverter();

    @Test
    void convertsFiftyMinCruiseToItsDatabaseLiteral() {
        assertThat(converter.convertToDatabaseColumn(TourCategory.FIFTY_MIN_CRUISE)).isEqualTo("50_MIN_CRUISE");
    }

    @Test
    void convertsFullDayCruiseToItsDatabaseLiteral() {
        assertThat(converter.convertToDatabaseColumn(TourCategory.FULL_DAY_CRUISE)).isEqualTo("FULL_DAY_CRUISE");
    }

    @Test
    void convertsDatabaseLiteralBackToFiftyMinCruise() {
        assertThat(converter.convertToEntityAttribute("50_MIN_CRUISE")).isEqualTo(TourCategory.FIFTY_MIN_CRUISE);
    }

    @Test
    void convertsDatabaseLiteralBackToFullDayCruise() {
        assertThat(converter.convertToEntityAttribute("FULL_DAY_CRUISE")).isEqualTo(TourCategory.FULL_DAY_CRUISE);
    }

    @Test
    void convertsNullsInBothDirections() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void rejectsUnknownDatabaseLiterals() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("NOT_A_CATEGORY"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
