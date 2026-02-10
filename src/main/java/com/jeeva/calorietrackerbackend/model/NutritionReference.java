package com.jeeva.calorietrackerbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "nutrition_reference",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "food_name")
    }
)
public class NutritionReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "food_name", nullable = false, unique = true)
    private String foodName;

    @Column(name = "calories_per_100g", nullable = false)
    private double caloriesPer100g;

    @Column(name = "protein_per_100g", nullable = false)
    private double proteinPer100g;

    @Column(name = "carbs_per_100g", nullable = false)
    private double carbsPer100g;

    @Column(name = "fat_per_100g", nullable = false)
    private double fatPer100g;

    @Column(name = "fiber_per_100g", nullable = false)
    private double fiberPer100g;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.foodName = this.foodName.toLowerCase().trim();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.foodName = this.foodName.toLowerCase().trim();
    }

    public NutritionReference() {
    }

    public NutritionReference(String foodName,
                              double caloriesPer100g,
                              double proteinPer100g,
                              double carbsPer100g,
                              double fatPer100g,
                              double fiberPer100g) {
        this.foodName = foodName;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
        this.fiberPer100g = fiberPer100g;
    }


    public Long getId() {
        return id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public double getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public double getFiberPer100g() {
        return fiberPer100g;
    }

    public void setFiberPer100g(double fiberPer100g) {
        this.fiberPer100g = fiberPer100g;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
