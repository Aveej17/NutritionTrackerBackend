package com.jeeva.calorietrackerbackend.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;


public class ErrorResponse {
    private String error;
    private int status;
    private long timestamp;

    // Custom constructor
    public ErrorResponse(String error, int status, long timestamp) {
        this.error = error;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters
    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setError(String error) {
        this.error = error;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

