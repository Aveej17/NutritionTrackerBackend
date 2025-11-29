package com.jeeva.calorietrackerbackend.repository;

import com.jeeva.calorietrackerbackend.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<Food, UUID> {
    @Override
    Food getById(UUID uuid);

    @Query(value = "select * from foods where user_id= :userid", nativeQuery = true )
    List<Food> getAllFoodByUser(@Param("userid") Long userid);
}
