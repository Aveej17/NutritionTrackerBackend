package com.jeeva.calorietrackerbackend.dto;

import java.util.List;

public class FoodResponse {
    private List<FoodWithNutrition> entries;
    private NutritionTotalsDto totals;

    public FoodResponse(
        List<FoodWithNutrition> entries,
        NutritionTotalsDto totals
    ) {
        this.entries = entries;
        this.totals = totals;
    }
}
