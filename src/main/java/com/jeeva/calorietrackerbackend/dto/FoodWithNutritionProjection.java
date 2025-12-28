package com.jeeva.calorietrackerbackend.dto;

import com.jeeva.calorietrackerbackend.model.MealType;

import java.util.Date;
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
    Date getDate();
    MealType getMealType();
}
