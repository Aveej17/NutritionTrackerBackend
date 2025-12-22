package com.jeeva.calorietrackerbackend.repository;

import com.jeeva.calorietrackerbackend.dto.FoodWithNutritionProjection;
import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.MealType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<Food, UUID>, JpaSpecificationExecutor<Food> {
    @Override
    Food getById(UUID uuid);

    Page<Food> findByUserUserId(Long userId, Pageable pageable);
    @Query(value = "select * from foods where user_id= :userid", nativeQuery = true )
    List<Food> getAllFoodByUser(@Param("userid") Long userid);

    @Query("SELECT f FROM Food f WHERE f.user.userId = :userId AND f.date BETWEEN :startDate AND :endDate AND f.mealType = :mealType")
            List<Food> findFoodsByUserAndDateRangeAndMealType(
            Long userId,
            Date startDate,
            Date endDate,
            MealType mealType
    );

    @Query("SELECT f FROM Food f WHERE f.user.userId = :userId AND f.date BETWEEN :startDate AND :endDate")
            List<Food> findFoodsByUserAndDateRange(
            Long userId,
            Date startDate,
            Date endDate
    );

    @Query("""
    SELECT\s
        f.name AS name,
        f.uuid AS uuid,
        f.imageUrl AS imageUrl,
        COALESCE(SUM(n.protein), 0) AS protein,
        COALESCE(SUM(n.fat), 0) AS fat,
        COALESCE(SUM(n.calories), 0) AS calories,
        COALESCE(SUM(n.carbs), 0) AS carbs,
        COALESCE(SUM(n.fiber), 0) AS fiber
    FROM Food f
    LEFT JOIN Nutrition n ON n.food.uuid = f.uuid
    WHERE f.user.userId = :userId
    GROUP BY f.uuid, f.name, f.imageUrl
""")
    List<FoodWithNutritionProjection> findFoodsWithNutrition(@Param("userId") Long userId);


    @Query(
            value = """
    SELECT 
        f.uuid AS uuid,
        f.name AS name,
        f.imageUrl AS imageUrl,
        COALESCE(SUM(n.protein), 0) AS protein,
        COALESCE(SUM(n.fat), 0) AS fat,
        COALESCE(SUM(n.calories), 0) AS calories,
        COALESCE(SUM(n.carbs), 0) AS carbs,
        COALESCE(SUM(n.fiber), 0) AS fiber
    FROM Food f
    LEFT JOIN Nutrition n ON n.food.uuid = f.uuid
    WHERE f.user.userId = :userId
    GROUP BY f.uuid, f.name, f.imageUrl
""",
            countQuery = """
    SELECT COUNT(f.uuid)
    FROM Food f
    WHERE f.user.userId = :userId
"""
    )
    Page<FoodWithNutritionProjection> findFoodsWithNutritionPaged(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
    SELECT
        f.name AS name,
        f.uuid AS uuid,
        f.imageUrl AS imageUrl,
        COALESCE(SUM(n.protein), 0) AS protein,
        COALESCE(SUM(n.fat), 0) AS fat,
        COALESCE(SUM(n.calories), 0) AS calories,
        COALESCE(SUM(n.carbs), 0) AS carbs,
        COALESCE(SUM(n.fiber), 0) AS fiber
    FROM Food f
    LEFT JOIN Nutrition n ON n.food.uuid = f.uuid
    WHERE f.user.userId = :userId
      AND f.date >= :startDate
      AND f.date < :endDate
    GROUP BY f.uuid, f.name, f.imageUrl
""")
    List<FoodWithNutritionProjection> findFoodsWithNutritionByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );


}
