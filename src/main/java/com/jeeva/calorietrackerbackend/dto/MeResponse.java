package com.jeeva.calorietrackerbackend.dto;

public record MeResponse(
    String name,
    String email,
    boolean isPrimeUser
) {}
