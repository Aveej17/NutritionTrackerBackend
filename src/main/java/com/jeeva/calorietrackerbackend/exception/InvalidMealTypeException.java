package com.jeeva.calorietrackerbackend.exception;

public class InvalidMealTypeException extends RuntimeException {
    public InvalidMealTypeException(String message) {
        super(message);
    }
}
