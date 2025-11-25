package com.jeeva.calorietrackerbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Service
public class AIService {

    @Value("${ai.api.url}")
    private String API_URL;
    @Value("${ai.api.key}")
    private String API_KEY;

    public String analyzeImage(String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();

        // Request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("HTTP-Referer", "YOUR_SITE_URL"); // Optional
        headers.set("X-Title", "YOUR_SITE_NAME"); // Optional

        // Construct request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "google/gemini-2.0-flash-001");

        // Construct messages list
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> messageContent = new HashMap<>();
        messageContent.put("role", "user");

        List<Map<String, Object>> contentList = new ArrayList<>();
        contentList.add(Map.of("type", "text", "text", "What is in this image?"));
        contentList.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl)));

        messageContent.put("content", contentList);
        messages.add(messageContent);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Make the request
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

        // Parse and return AI response
        return parseAIResponse(response.getBody());
    }

    private String parseAIResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Error parsing AI response";
        }
    }
}
