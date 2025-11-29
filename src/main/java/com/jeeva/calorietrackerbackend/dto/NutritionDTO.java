package com.jeeva.calorietrackerbackend.dto;

public class NutritionDTO {

    private String name;
    private Long protein;
    private Long fat;
    private Long calories;
    private Long carbs;
    private Long fiber;

    public String getName() {
        return name;
    }

    public Long getProtein() {
        return protein;
    }

    public Long getFat() {
        return fat;
    }

    public Long getCalories() {
        return calories;
    }

    public Long getCarbs() {
        return carbs;
    }

    public Long getFiber() {
        return fiber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProtein(Long protein) {
        this.protein = protein;
    }

    public void setFat(Long fat) {
        this.fat = fat;
    }

    public void setCalories(Long calories) {
        this.calories = calories;
    }

    public void setCarbs(Long carbs) {
        this.carbs = carbs;
    }

    public void setFiber(Long fiber) {
        this.fiber = fiber;
    }
}
