package com.jeeva.calorietrackerbackend.repository;

import com.jeeva.calorietrackerbackend.model.Nutrition;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NutritionRepository extends JpaRepository<Nutrition, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO nutritions (name, calories, carbs, protein, fat, fiber, food_id)
        VALUES (:#{#items[0].name}, :#{#items[0].calories}, :#{#items[0].carbs},
                :#{#items[0].protein}, :#{#items[0].fat}, :#{#items[0].fiber}, :foodId)
        """, nativeQuery = true)
    void bulkInsert(@Param("items") List<Nutrition> items,
                    @Param("foodId") Long foodId);

}
