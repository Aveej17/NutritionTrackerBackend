package com.jeeva.calorietrackerbackend.dto;

import java.util.UUID;

public interface FoodWithNutritionProjection {

    String getName();
    UUID getUuid();
    String getImageUrl();

    Long getProtein();
    Long getFat();
    Long getCalories();
    Long getCarbs();
    Long getFiber();
}
