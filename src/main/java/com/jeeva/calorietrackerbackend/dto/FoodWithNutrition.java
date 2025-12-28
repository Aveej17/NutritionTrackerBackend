package com.jeeva.calorietrackerbackend.dto;

import com.jeeva.calorietrackerbackend.model.MealType;

import java.util.Date;

public class FoodWithNutrition {

    private String uuid;
    private String name;
    private String imageUrl;

    private Long protein = 0L;
    private Long fat = 0L;
    private Long calories = 0L;
    private Long carbs = 0L;
    private Long fiber = 0L;

    private Date date;

    private MealType mealType;
    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public Date getDate(){
        return date;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public void setDate(Date date){
        this.date = date;
    }

    public void setMealType(MealType mealType){
        this.mealType = mealType;
    }
}
