package com.jeeva.calorietrackerbackend.model;


import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "nutritions")
public class Nutrition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_uuid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Food food;
    private String name;
    private Long protein;
    private Long fat;
    private Long calories;
    private Long carbs;
    private Long fiber;


    @Override
    public String toString() {
        return "Nutrition{" +
                "id=" + id +
                ", foodUuid=" + (food != null ? food.getUuid() : null) +
                ", name='" + name + '\'' +
                ", protein=" + protein +
                ", fat=" + fat +
                ", calories=" + calories +
                ", carbs=" + carbs +
                ", fiber=" + fiber +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getProtein() {
        return protein;
    }

    public Long getFat() {
        return fat;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProtein(Long protein) {
        this.protein = protein;
    }

    public void setFat(Long fat) {
        this.fat = fat;
    }

    public void setCalories(Long calories) {
        this.calories = calories;
    }

    public void setCarbs(Long carbs) {
        this.carbs = carbs;
    }

    public void setFiber(Long fiber) {
        this.fiber = fiber;
    }

    public Long getCalories() {
        return calories;
    }

    public Long getCarbs() {
        return carbs;
    }

    public Long getFiber() {
        return fiber;
    }
}
