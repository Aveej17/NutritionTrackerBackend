package com.jeeva.calorietrackerbackend.dto;

public record UpdateGoalRequest(
    int calories,
    int protein,
    int carbs,
    int fat
) {}
