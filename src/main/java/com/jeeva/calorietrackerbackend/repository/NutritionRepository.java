package com.jeeva.calorietrackerbackend.repository;

import com.jeeva.calorietrackerbackend.model.Nutrition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutritionRepository extends JpaRepository<Nutrition, Long> {

}
