package com.durustours.backend.domain;

/**
 * The bookable tour products offered by DurusTours.
 */
public enum TourCategory {

    BRIDGES_TOUR(CruiseType.RIVER_CRUISE, 50, false),
    BURMESTER_COMBO(CruiseType.RIVER_CRUISE, 50, true),
    REGUA_FULL_DAY(CruiseType.FULL_DAY_CRUISE, null, false),
    PINHAO_FULL_DAY(CruiseType.FULL_DAY_CRUISE, null, false);

    private final CruiseType cruiseType;
    private final Integer standardDurationMinutes;
    private final boolean comboActivationRequired;

    TourCategory(CruiseType cruiseType, Integer standardDurationMinutes, boolean comboActivationRequired) {
        this.cruiseType = cruiseType;
        this.standardDurationMinutes = standardDurationMinutes;
        this.comboActivationRequired = comboActivationRequired;
    }

    public CruiseType getCruiseType() {
        return cruiseType;
    }

    public Integer getStandardDurationMinutes() {
        return standardDurationMinutes;
    }

    /**
     * Only BURMESTER_COMBO has a voucher whose 48h validity window starts at first
     * activation, either at the boat dock or the wine cellar.
     */
    public boolean requiresComboActivation() {
        return comboActivationRequired;
    }

    public boolean isFullDay() {
        return cruiseType == CruiseType.FULL_DAY_CRUISE;
    }

    public enum CruiseType {
        RIVER_CRUISE,
        FULL_DAY_CRUISE
    }
}
