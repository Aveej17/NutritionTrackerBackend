package com.jeeva.calorietrackerbackend.dto;

public class NutritionTotalsDto {

    private final long calories;
    private final long protein;
    private final long carbs;
    private final long fat;

    public NutritionTotalsDto(Object calories,
                              Object protein,
                              Object carbs,
                              Object fat) {
        this.calories = toLong(calories);
        this.protein  = toLong(protein);
        this.carbs    = toLong(carbs);
        this.fat      = toLong(fat);
    }

    private long toLong(Object value) {
        return value instanceof Number
                ? ((Number) value).longValue()
                : 0L;
    }

    public long getCalories() { return calories; }
    public long getProtein()  { return protein; }
    public long getCarbs()    { return carbs; }
    public long getFat()      { return fat; }
}
