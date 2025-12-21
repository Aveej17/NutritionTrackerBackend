package com.jeeva.calorietrackerbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;


@Getter
@Setter
@Entity
@Table(name = "users")
@Data
public class User {
    // Getters & Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private boolean isPrimeUser = false;

    public void setUserId(Long userId) { this.userId = userId; }

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setIsPrimeUser(boolean primeUser){
        this.isPrimeUser = primeUser;
    }


    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean getIsPrimeUser(){
        return isPrimeUser;
    }

    public String getPassword() {
        return password;
    }
}
