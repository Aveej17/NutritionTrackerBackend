package com.jeeva.calorietrackerbackend.dto;

public record GoalResponse(
    int calories,
    int protein,
    int carbs,
    int fat,
    boolean editable   // 🔑 frontend uses this
) {}
