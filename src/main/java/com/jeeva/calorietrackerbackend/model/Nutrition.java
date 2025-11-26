package com.jeeva.calorietrackerbackend.model;


import jakarta.persistence.*;

@Entity
@Table(name = "nutrition")
public class Nutrition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid")
    private Food food;
    private String name;

    private Long protein;

    private Long fat;

    private Long calories;

    private Long carbs;
    private Long fiber;


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
