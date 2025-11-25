package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;
    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeImage(@RequestParam String imageUrl) {
        String aiResponse = aiService.analyzeImage(imageUrl);
        return ResponseEntity.ok(aiResponse);
    }
}