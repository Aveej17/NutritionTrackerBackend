package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.dto.FoodDTO;
import com.jeeva.calorietrackerbackend.dto.FoodWithNutrition;
import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.MealType;
import com.jeeva.calorietrackerbackend.service.FoodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
            @RequestParam("mealType") String mealType,
            @RequestParam("name") String name){

        log.debug("Received upload-image request ");

        try {
            Food food = foodService.addFood(imageFile, notes, mealType, name);

            log.info("Food uploaded successfully");
            return ResponseEntity.ok("Food saved successfully");

        } catch (Exception e) {
            log.error("Error uploading food image : {}",  e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving food: " + e.getMessage());
        }
    }

    @GetMapping("/all")
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

    @GetMapping("/all-new")
    public ResponseEntity<List<FoodWithNutrition>> getFoodsWithNutrition(){
        log.debug("Fetching the food Details");
        try{
            List<FoodWithNutrition> foodWithNutrition = foodService.getFoodsWithNutrition();

            log.info("Food details fetched Successfully");
            return ResponseEntity.ok(foodWithNutrition);
        }
        catch(Exception e) {
            log.error("Error fetching food details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all-new-paged")
    public ResponseEntity<Page<FoodWithNutrition>> getFoodsWithNutritionPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FoodWithNutrition> foodPage =
                foodService.getFoodsWithNutritionPaged(page, size);

        return ResponseEntity.ok(foodPage);
    }



    @GetMapping("/basic")
    public List<FoodDTO> getFoods(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) MealType mealType
    ) {
        return foodService.getFoods(startDate, endDate, mealType);
    }

    @GetMapping("/filter")
    public Page<FoodDTO> getFoodsDynamic(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            MealType mealType,

            @RequestParam(required = false)
            String keyword,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        return foodService.getFoodsDynamic(
                startDate,
                endDate,
                mealType,
                keyword,
                page,
                size
        );
    }


    @GetMapping()
    public ResponseEntity<Page<FoodDTO>> getFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Fetching paginated food details");

        Page<FoodDTO> foodDTOPage = foodService.getFoods(page, size);

        log.info("Food details fetched successfully (page={}, size={})", page, size);
        return ResponseEntity.ok(foodDTOPage);

    }

    @DeleteMapping()
    public ResponseEntity<String> deleteFood(@RequestParam("food-id") String foodId){

        try{
            log.debug("Calling Delete food Service");
            log.info("foodId : {}",foodId);
            UUID uuid = UUID.fromString(foodId);
            foodService.deleteFood(uuid);
            return ResponseEntity.ok("food deleted successfully");
        }
        catch(Exception e){
            log.error("Unable to delete the food {}", foodId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to delete food");
        }
    }
}
