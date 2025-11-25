package com.jeeva.calorietrackerbackend.service;

import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.MealType;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.FoodRepository;
import com.jeeva.calorietrackerbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
    @Value("${publicurl}")
    private String publicUrl;

    public Food addFood(MultipartFile multipartFile, String userMail) throws Exception {
        log.debug("Starting addFood for user: {}", userMail);

        // Fetch the User entity
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userMail);
                    return new RuntimeException("User not found");
                });

        log.debug("User fetched successfully: {}", user.getEmail());

        //TODO
        //1. Upload the image to a s3
        String key;
        try {
            key = nutritionImageService.uploadUserImage(multipartFile, user.getUserId());
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }
        String url = getPublicUrl(key);
        //2. Generate the calorie details;
        List<Map<String, Object>> foods = calorieService.analyzeImageGetNutrition(url);

        Food food = getFood(foods, user, url);


        // Save to database
        Food savedFood = foodRepository.save(food);
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

// Numeric values must be converted properly
        food.setCalories(String.valueOf(item.get("calories")));

        food.setProtein(String.valueOf(item.get("protein")));
        food.setFat(String.valueOf(item.get("fat")));
        return food;
    }

    public String getPublicUrl(String key) {
        return publicUrl + key;
    }

}
