package com.jeeva.calorietrackerbackend.repository;

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

}
