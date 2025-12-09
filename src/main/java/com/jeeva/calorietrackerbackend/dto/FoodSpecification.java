package com.jeeva.calorietrackerbackend.dto;

import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.MealType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class FoodSpecification {

    public static Specification<Food> userEquals(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("userId"), userId);
    }

    public static Specification<Food> dateBetween(Date start, Date end) {
        return (root, query, cb) -> cb.between(root.get("date"), start, end);
    }

    public static Specification<Food> mealTypeEquals(MealType mealType) {
        return (root, query, cb) -> 
                mealType == null ? cb.conjunction() : cb.equal(root.get("mealType"), mealType);
    }

    public static Specification<Food> notesContains(String keyword) {
        return (root, query, cb) ->
                keyword == null ? cb.conjunction() : cb.like(cb.lower(root.get("notes")), "%" + keyword.toLowerCase() + "%");
    }
}
