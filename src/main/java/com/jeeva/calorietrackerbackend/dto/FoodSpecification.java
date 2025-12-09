package com.jeeva.calorietrackerbackend.dto;

import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.MealType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class FoodSpecification {


    public static Specification<Food> userEquals(Long userId) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("userId"), userId);
    }


    public static Specification<Food> dateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {

            if (start == null && end == null) {
                return cb.conjunction(); // no filter
            }

            if (start != null && end == null) {
                Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
                return cb.greaterThanOrEqualTo(root.get("date"), startDate);
            }

            if (start == null) {
                Date endDate = Date.from(end.plusDays(1)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());
                return cb.lessThan(root.get("date"), endDate);
            }

            Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(end.plusDays(1)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());

            return cb.between(root.get("date"), startDate, endDate);
        };
    }

    public static Specification<Food> mealTypeEquals(MealType mealType) {
        return (root, query, cb) ->
                mealType == null
                        ? cb.conjunction()
                        : cb.equal(root.get("mealType"), mealType);
    }

    public static Specification<Food> notesContains(String keyword) {
        return (root, query, cb) ->

                (keyword == null || keyword.isBlank())
                        ? cb.conjunction()
                        : cb.like(
                        cb.lower(root.get("notes")),
                        "%" + keyword.toLowerCase() + "%"
                );
    }
}
