package com.jeeva.calorietrackerbackend.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Find all foods containing keyword, sorted by relevance
    // Returns foods ordered by: exact match > starts with > contains, then by length, then alphabetically
    @Query(value = """
        SELECT * FROM nutrition_reference
        WHERE LOWER(food_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY 
            CASE 
                WHEN LOWER(food_name) = LOWER(:keyword) THEN 0
                WHEN LOWER(food_name) LIKE LOWER(CONCAT(:keyword, '%')) THEN 1
                ELSE 2
            END,
            LENGTH(food_name) ASC,
            food_name ASC
        """, nativeQuery = true)
    List<NutritionReference> findBestMatchingFoods(@Param("keyword") String keyword);
}
