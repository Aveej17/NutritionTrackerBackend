package com.jeeva.calorietrackerbackend.controller;


import com.jeeva.calorietrackerbackend.dto.ResponsePayload;
import com.jeeva.calorietrackerbackend.service.CalorieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tracker")
public class CalorieController {
    private static final Logger logger = LoggerFactory.getLogger(CalorieController.class);

    private final CalorieService calorieService;

    public CalorieController(CalorieService calorieService){
        this.calorieService = calorieService;
    }

    @PostMapping()
    public ResponseEntity<ResponsePayload> analyzeImageUrl(@RequestParam String imageUrl){
        List<Map<String, Object>> nutritionData = new ArrayList<>();
        ResponsePayload responsePayload = new ResponsePayload();
        try{
            nutritionData = calorieService.analyzeImageGetNutrition(imageUrl);
            responsePayload.setNutritionData(nutritionData);
            responsePayload.setResponseCode("SUCCESS");
            responsePayload.setResponseMessage("Successfully fetched nutrientional value of the foods");
            return new ResponseEntity<>(responsePayload, HttpStatus.OK);
        }
        catch(Exception e){
            logger.error("Error analyzing food image: {}", e.getMessage(), e);

            responsePayload.setNutritionData(nutritionData);
            responsePayload.setResponseCode("ERROR");
            responsePayload.setResponseMessage("Error analyzing food image : " + e.getMessage());
            return new ResponseEntity<>(responsePayload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
