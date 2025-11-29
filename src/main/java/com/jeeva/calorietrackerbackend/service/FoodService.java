package com.jeeva.calorietrackerbackend.service;

import com.jeeva.calorietrackerbackend.dto.FoodDTO;
import com.jeeva.calorietrackerbackend.dto.NutritionDTO;
import com.jeeva.calorietrackerbackend.exception.InvalidMealTypeException;
import com.jeeva.calorietrackerbackend.exception.UserNotFoundException;
import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.MealType;
import com.jeeva.calorietrackerbackend.model.Nutrition;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.FoodRepository;
import com.jeeva.calorietrackerbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FoodService {

    private static final Logger log = LoggerFactory.getLogger(FoodService.class);

    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CalorieService calorieService;
    @Autowired
    private NutritionImageService nutritionImageService;
    @Autowired
    private NutritionService nutritionService;
    @Value("${publicurl}")
    private String publicUrl;

    public Food addFood(MultipartFile multipartFile, String notes, String mealType) throws Exception {
        log.debug("Starting addFood");
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        // Fetch the User entity
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userMail);
                    return new RuntimeException("User not found");
                });

        log.debug("User fetched successfully: {}", user.getEmail());

        try {
            MealType meal = MealType.valueOf(mealType.toUpperCase());
        } catch (Exception e) {
            throw new InvalidMealTypeException("MealType must be: breakfast, lunch, dinner, snack");
        }
        //TODO
        //1. Upload the image to a s3
        String key;
        try {
            key = nutritionImageService.uploadUserImage(multipartFile, user.getUserId());
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }
        String url = getPublicUrl(key);
        Food food = new Food();
        food.setNotes(notes);
        food.setUser(user);
        food.setImageUrl(url);
        food.setDate(new Date());
        food.setMealType(MealType.valueOf(mealType.toUpperCase()));
        // Save to database
        Food savedFood = foodRepository.save(food);
        //2. generate the nutrition details;
        List<Map<String, Object>> foods = calorieService.analyzeImageGetNutrition(url);
        nutritionService.addNutritionDetails(foods, savedFood);
        log.info("Food saved successfully for user {}: Food ID {}", userMail, savedFood.getUuid());

        return savedFood;
    }

    private static Food getFood(List<Map<String, Object>> foods, User user, String url) {
        if (foods == null || foods.isEmpty()) {
            throw new RuntimeException("No food items detected in image.");
        }
        Map<String, Object> item = foods.get(0); // first detected food
        Food food = new Food();
        food.setUser(user);
        food.setImageUrl(url);
        food.setMealType(MealType.BREAKFAST);  // Need to set dynamically

        return food;
    }

    public String getPublicUrl(String key) {
        return publicUrl + key;
    }

    public List<FoodDTO> getFoods() {
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();

        try {

//            Not needed as user already verified
            User user = userRepository.findByEmail(userMail).orElseThrow(
                    () -> {
                        log.error("user not found");
                        return new UserNotFoundException("user not found");

                    }
            );

            log.info("Getting food Details for user {}",userMail);
            List<Food> foods = foodRepository.getAllFoodByUser(user.getUserId());

            List<FoodDTO> foodDTOList = new ArrayList<>();
            for (Food food : foods){
                FoodDTO f = new FoodDTO();
                f.setUuid(food.getUuid());
                f.setImageUrl(food.getImageUrl());
                List<NutritionDTO> n = nutritionService.getNutrition(food.getUuid());
                f.setNutritionDTOList(n);
                foodDTOList.add(f);
            }
            log.info(foods.toString());
            log.info("Food DTO : "+foodDTOList);
            return foodDTOList;
        }
        catch(Exception e){
            log.error("Some Error occurred while fetching food for a user {}", userMail);
            return null;
        }

    }
}
