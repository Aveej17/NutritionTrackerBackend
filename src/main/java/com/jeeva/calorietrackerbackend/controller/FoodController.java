package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.service.FoodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private static final Logger log = LoggerFactory.getLogger(FoodController.class);

    @Autowired
    private FoodService foodService;

    @PostMapping(
            value = "/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadImage(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("email") String userMail) {

        log.debug("Received upload-image request from email: {}", userMail);

        try {
            Food food = foodService.addFood(imageFile, userMail);

            log.info("Food uploaded successfully by user {}: Food ID {}", userMail, food.getUuid());
            return ResponseEntity.ok("Food saved successfully with ID: " + food.getUuid());

        } catch (Exception e) {
            log.error("Error uploading food image for user {}: {}", userMail, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving food: " + e.getMessage());
        }
    }
}
