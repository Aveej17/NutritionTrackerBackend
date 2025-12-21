package com.jeeva.calorietrackerbackend.dto;

public class AuthResponse {
    private String token;

    private String name;

    private String email;

    private boolean isPrimeUser;

    public AuthResponse(String token, String name, String email, boolean isPrimeUser) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.isPrimeUser = isPrimeUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean getIsPrimeUser(){return isPrimeUser;}
}