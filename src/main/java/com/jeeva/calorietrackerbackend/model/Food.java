package com.jeeva.calorietrackerbackend.model;


import jakarta.persistence.*;


import java.util.UUID;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    private String calories;
    private String fat;
    private String protein;
    private String imageUrl;
    private String notes;

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UUID getUuid() {
        return uuid;
    }


    public MealType getMealType() {
        return mealType;
    }

    public String getCalories() {
        return calories;
    }

    public String getFat() {
        return fat;
    }

    public String getProtein() {
        return protein;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getNotes() {
        return notes;
    }
}
