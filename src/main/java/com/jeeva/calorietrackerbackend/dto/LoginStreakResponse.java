package com.jeeva.calorietrackerbackend.dto;

import java.util.Date;

public class LoginStreakResponse {
    private int currentStreak;
    private int longestStreak;
    private Date lastLoginDate;
    private String message;

    public LoginStreakResponse() {
    }

    public LoginStreakResponse(int currentStreak, int longestStreak, Date lastLoginDate, String message) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastLoginDate = lastLoginDate;
        this.message = message;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoginStreakResponse{" +
                "currentStreak=" + currentStreak +
                ", longestStreak=" + longestStreak +
                ", lastLoginDate=" + lastLoginDate +
                ", message='" + message + '\'' +
                '}';
    }
}
