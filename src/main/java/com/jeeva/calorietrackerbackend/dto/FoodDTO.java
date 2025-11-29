package com.jeeva.calorietrackerbackend.dto;

import java.util.List;
import java.util.UUID;

public class FoodDTO {
    private UUID uuid;
    private String imageUrl;
    private List<NutritionDTO> nutritionDTOList;

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setNutritionDTOList(List<NutritionDTO> nutritionDTOList) {
        this.nutritionDTOList = nutritionDTOList;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<NutritionDTO> getNutritionDTOList() {
        return nutritionDTOList;
    }
}
