package com.durustours.backend.domain;

/**
 * Coarse catalog grouping for tours. Individual products within a category
 * (e.g. the plain Bridges Tour vs. the Burmester Combo, both 50-minute
 * cruises) are distinguished by {@link Tour#isCombo()} and title, not by a
 * separate enum constant per product.
 */
public enum TourCategory {

    FIFTY_MIN_CRUISE,
    FULL_DAY_CRUISE;

    public boolean isFullDay() {
        return this == FULL_DAY_CRUISE;
    }
}
