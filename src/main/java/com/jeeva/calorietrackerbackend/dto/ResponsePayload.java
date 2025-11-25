package com.jeeva.calorietrackerbackend.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResponsePayload {
    private String responseMessage;
    private String responseCode;
    private List<Map<String, Object>> nutritionData;

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setNutritionData(List<Map<String, Object>> nutritionData) {
        this.nutritionData = nutritionData;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public List<Map<String, Object>> getNutritionData() {
        return nutritionData;
    }
}
