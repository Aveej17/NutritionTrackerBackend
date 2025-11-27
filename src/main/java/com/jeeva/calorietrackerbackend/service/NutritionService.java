package com.jeeva.calorietrackerbackend.service;


import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.Nutrition;
import com.jeeva.calorietrackerbackend.repository.NutritionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NutritionService {

    private static final Logger log = LoggerFactory.getLogger(NutritionService.class);

    @Autowired
    private NutritionRepository nutritionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addNutritionDetails(List<Map<String, Object>> foods, Food food) {

        log.info("Adding {} nutrition entries for Food UUID: {}",
                foods.size(), food.getUuid());

//        for (Map<String, Object> mp : foods) {
//            try {
//                Nutrition nutrition = new Nutrition();
//                nutrition.setName((String) mp.get("name"));
//                nutrition.setCalories(toLong(mp.get("calories")));
//                nutrition.setCarbs(toLong(mp.get("carbs")));
//                nutrition.setProtein(toLong(mp.get("protein")));
//                nutrition.setFat(toLong(mp.get("fat")));
//                nutrition.setFiber(toLong(mp.get("fiber")));
//                nutrition.setFood(food);
//
//                nutritionRepository.save(nutrition);
//
//                log.info("Saved nutrition item '{}' for Food {}",
//                        nutrition.getName(), food.getUuid());
//
//
//            } catch (Exception ex) {
//                log.error("Failed to save nutrition item for Food {}. Data: {}. Error: {}",
//                        food.getUuid(), mp, ex.toString());
//            }
//        }

        List<Nutrition> nutritionList = foods.stream().map(mp -> {
            Nutrition n = new Nutrition();
            n.setName((String) mp.get("name"));
            n.setCalories(toLong(mp.get("calories")));
            n.setCarbs(toLong(mp.get("carbs")));
            n.setProtein(toLong(mp.get("protein")));
            n.setFat(toLong(mp.get("fat")));
            n.setFiber(toLong(mp.get("fiber")));
            n.setFood(food);   // set the FK to Food entity
            return n;
        }).collect(Collectors.toList());

        try {
            nutritionRepository.saveAll(nutritionList);
            log.info("Successfully inserted {} nutrition entries for Food UUID: {}",
                    nutritionList.size(), food.getUuid());
        } catch (Exception ex) {
            log.error("Failed to insert nutrition entries for Food UUID: {}. Error: {}",
                    food.getUuid(), ex.getMessage(), ex);
        }


    }

    private Long toLong(Object value) {
        if (value == null) return null;

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        throw new IllegalArgumentException("Invalid numeric value: " + value);
    }



    public void addNutritionDetailsNative(List<Map<String, Object>> foods, Food food) {

        if (foods == null || foods.isEmpty()) {
            log.warn("No nutrition data provided for Food UUID: {}", food.getUuid());
            return;
        }

        log.info("Adding {} nutrition entries for Food UUID: {}", foods.size(), food.getUuid());

        StringBuilder sql = new StringBuilder(
                "INSERT INTO nutritions (name, calories, carbs, protein, fat, fiber, food_uuid) VALUES "
        );


        List<Object> params = new ArrayList<>();

        for (int i = 0; i < foods.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("(?, ?, ?, ?, ?, ?, ?)");

            Map<String, Object> mp = foods.get(i);
            params.add(mp.get("name"));
            params.add(toLong(mp.get("calories")));
            params.add(toLong(mp.get("carbs")));
            params.add(toLong(mp.get("protein")));
            params.add(toLong(mp.get("fat")));
            params.add(toLong(mp.get("fiber")));
            params.add(food.getUuid());
        }

        try {
            int rowsInserted = jdbcTemplate.update(sql.toString(), params.toArray());
            log.info("Successfully inserted {} nutrition entries for Food UUID: {}", rowsInserted, food.getUuid());
        } catch (Exception ex) {
            log.error("Failed to insert nutrition entries for Food UUID: {}. Error: {}", food.getUuid(), ex.getMessage(), ex);
        }
    }
}
