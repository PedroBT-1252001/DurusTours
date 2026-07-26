package com.durustours.backend.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Maps {@link TourCategory} to the catalog's database literals. A converter is used
 * instead of {@code @Enumerated(STRING)} because "50_MIN_CRUISE" is not a legal Java
 * enum constant name (identifiers cannot start with a digit).
 */
@Converter(autoApply = true)
public class TourCategoryConverter implements AttributeConverter<TourCategory, String> {

    @Override
    public String convertToDatabaseColumn(TourCategory category) {
        if (category == null) {
            return null;
        }
        return switch (category) {
            case FIFTY_MIN_CRUISE -> "50_MIN_CRUISE";
            case FULL_DAY_CRUISE -> "FULL_DAY_CRUISE";
        };
    }

    @Override
    public TourCategory convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        return switch (dbValue) {
            case "50_MIN_CRUISE" -> TourCategory.FIFTY_MIN_CRUISE;
            case "FULL_DAY_CRUISE" -> TourCategory.FULL_DAY_CRUISE;
            default -> throw new IllegalArgumentException("Unknown tour category: " + dbValue);
        };
    }
}
