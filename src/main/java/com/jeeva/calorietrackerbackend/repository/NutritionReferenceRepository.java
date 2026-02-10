package com.jeeva.calorietrackerbackend.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeeva.calorietrackerbackend.model.NutritionReference;

@Repository
public interface NutritionReferenceRepository
        extends JpaRepository<NutritionReference, Long> {

    // Exact match (normalized lowercase)
    Optional<NutritionReference> findByFoodName(String foodName);

    // Case-insensitive match (extra safety)
    Optional<NutritionReference> findByFoodNameIgnoreCase(String foodName);

    // Search suggestions (useful for autocomplete)
    List<NutritionReference> findByFoodNameContainingIgnoreCase(String keyword);

    // Check existence
    boolean existsByFoodNameIgnoreCase(String foodName);
}
