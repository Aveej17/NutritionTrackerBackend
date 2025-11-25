package com.jeeva.calorietrackerbackend.service;

import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.FoodRepository;
import com.jeeva.calorietrackerbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FoodService {

    private static final Logger log = LoggerFactory.getLogger(FoodService.class);

    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;

    public Food addFood(MultipartFile multipartFile, String userMail){
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
        //2. provide the url to the AI and get the nutrition details
        //3. dummy persisting as of now need a real implementation



        // Create Food entity
        Food food = new Food();
        food.setUser(user);
        food.setCalories("9");
        food.setFat("19");
        food.setImageUrl("/jkdvn");
        food.setProtein("90");
        food.setNotes("hi");

        // Save to database
        Food savedFood = foodRepository.save(food);
        log.info("Food saved successfully for user {}: Food ID {}", userMail, savedFood.getUuid());

        return savedFood;
    }

}
