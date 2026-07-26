package com.durustours.backend.dto;

import com.durustours.backend.domain.Tour;
import com.durustours.backend.domain.TourCategory;

import java.math.BigDecimal;

public record TourDTO(
        Long id,
        TourCategory category,
        String title,
        String description,
        Integer durationMinutes,
        String durationLabel,
        BigDecimal basePrice,
        boolean combo,
        boolean active
) {

    public static TourDTO from(Tour tour) {
        return new TourDTO(
                tour.getId(),
                tour.getCategory(),
                tour.getTitle(),
                tour.getDescription(),
                tour.getDurationMinutes(),
                tour.getDurationLabel(),
                tour.getBasePrice(),
                tour.isCombo(),
                tour.isActive()
        );
    }
}
