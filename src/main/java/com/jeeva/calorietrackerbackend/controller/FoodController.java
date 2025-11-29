package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.dto.FoodDTO;
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

import java.util.List;

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
            @RequestParam("notes") String notes,
            @RequestParam("mealType") String mealType){

        log.debug("Received upload-image request ");

        try {
            Food food = foodService.addFood(imageFile, notes, mealType);

            log.info("Food uploaded successfully");
            return ResponseEntity.ok("Food saved successfully");

        } catch (Exception e) {
            log.error("Error uploading food image : {}",  e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving food: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<List<FoodDTO>> getFoods(){
        log.debug("Fetching the food Details");
        try{
            List<FoodDTO> foodDTOList = foodService.getFoods();

            log.info("Food details fetched Successfully");
            return ResponseEntity.ok(foodDTOList);
        }
        catch(Exception e) {
            log.error("Error fetching food details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
