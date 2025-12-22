package com.jeeva.calorietrackerbackend.service;

import com.jeeva.calorietrackerbackend.dto.*;
import com.jeeva.calorietrackerbackend.exception.InvalidMealTypeException;
import com.jeeva.calorietrackerbackend.exception.UserNotFoundException;
import com.jeeva.calorietrackerbackend.model.Food;
import com.jeeva.calorietrackerbackend.model.MealType;

import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.FoodRepository;
import com.jeeva.calorietrackerbackend.repository.UserRepository;

import com.jeeva.calorietrackerbackend.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class FoodService {

    private static final Logger log = LoggerFactory.getLogger(FoodService.class);

    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CalorieService calorieService;
    @Autowired
    private NutritionImageService nutritionImageService;
    @Autowired
    private NutritionService nutritionService;

    public Food addFood(MultipartFile multipartFile, String notes, String mealType, String name) throws Exception {
        log.debug("Starting addFood");
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        // Fetch the User entity
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userMail);
                    return new RuntimeException("User not found");
                });

        log.debug("User fetched successfully: {}", user.getEmail());

        try {
            log.debug("Meal type is : {}", mealType);
            MealType meal = MealType.valueOf(mealType.toUpperCase());
        } catch (Exception e) {
            throw new InvalidMealTypeException("MealType must be: breakfast, lunch, dinner, snack");
        }
        //TODO
        //1. Upload the image to a s3
        String url;
        try {
            url = nutritionImageService.uploadImage(multipartFile, user.getUserId());
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }

        Food food = new Food();
        food.setNotes(notes);
        food.setUser(user);
        food.setImageUrl(url);
        food.setName(name);
        food.setDate(new Date());
        food.setMealType(MealType.valueOf(mealType.toUpperCase()));
        // Save to database
        Food savedFood = foodRepository.save(food);
        //2. generate the nutrition details;
        List<Map<String, Object>> foods = calorieService.analyzeImageGetNutrition(url);
        nutritionService.addNutritionDetails(foods, savedFood);
        log.info("Food saved successfully for user {}: Food ID {}", userMail, savedFood.getUuid());

        return savedFood;
    }

    private static Food getFood(List<Map<String, Object>> foods, User user, String url) {
        if (foods == null || foods.isEmpty()) {
            throw new RuntimeException("No food items detected in image.");
        }
        Map<String, Object> item = foods.get(0); // first detected food
        Food food = new Food();
        food.setUser(user);
        food.setImageUrl(url);
        food.setMealType(MealType.BREAKFAST);  // Need to set dynamically

        return food;
    }

    public List<FoodWithNutrition> getFoodsWithNutrition() {
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        try {

//            Not needed as user already verified
            User user = userRepository.findByEmail(userMail).orElseThrow(
                    () -> {
                        log.error("user not found");
                        return new UserNotFoundException("user not found");

                    }
            );

            log.info("Getting food Details for user {}",userMail);
//            List<Food> foods = foodRepository.getAllFoodByUser(user.getUserId());
//
//
//            List<FoodWithNutrition> foodWithNutritionsList = new ArrayList<>();
//
//            for (Food food : foods){
//                FoodWithNutrition f = new FoodWithNutrition();
//                f.setName(food.getName());
//                f.setUuid(food.getUuid().toString());
//                f.setImageUrl(food.getImageUrl());
//                List<NutritionDTO> nutritions =
//                        nutritionService.getNutrition(food.getUuid());
//
//                for (NutritionDTO nut : nutritions) {
//
//                    if (nut.getProtein() != null) {
//                        f.setProtein(f.getProtein() + nut.getProtein());
//                    }
//
//                    if (nut.getFat() != null) {
//                        f.setFat(f.getFat() + nut.getFat());
//                    }
//
//                    if (nut.getCalories() != null) {
//                        f.setCalories(f.getCalories() + nut.getCalories());
//                    }
//
//                    if (nut.getCarbs() != null) {
//                        f.setCarbs(f.getCarbs() + nut.getCarbs());
//                    }
//
//                    if (nut.getFiber() != null) {
//                        f.setFiber(f.getFiber() + nut.getFiber());
//                    }
//                }
//
//
//
//                foodWithNutritionsList.add(f);
//            }
            List<FoodWithNutritionProjection> projections =
                    foodRepository.findFoodsWithNutrition(user.getUserId());

            return projections.stream().map(p -> {
                FoodWithNutrition f = new FoodWithNutrition();
                f.setName(p.getName());
                f.setUuid(p.getUuid().toString());
                f.setImageUrl(p.getImageUrl());
                f.setProtein(p.getProtein());
                f.setFat(p.getFat());
                f.setCalories(p.getCalories());
                f.setCarbs(p.getCarbs());
                f.setFiber(p.getFiber());
                return f;
            }).toList();
//            log.info(foods.toString());
//            log.info("Food With Nutrition : {}", foodWithNutritionsList);
//            return foodWithNutritionsList;

        }
        catch(Exception e){
            log.error("Some Error occurred while fetching food for a user {}", userMail);
            return null;
        }
    }


    public Page<FoodWithNutrition> getFoodsWithNutritionPaged(int page, int size) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        log.debug("Fetching paged food nutrition data for user={}, page={}, size={}",
                email, page, size);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for email={}", email);
                    return new UserNotFoundException("user not found");
                });

        if (size > 50) {
            log.warn("Page size exceeded limit. userId={}, requestedSize={}",
                    user.getUserId(), size);
            throw new IllegalArgumentException("Page size cannot exceed 50");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("uuid").descending()
        );

        log.debug("Executing paged food query for userId={}", user.getUserId());

        Page<FoodWithNutritionProjection> pageData =
                foodRepository.findFoodsWithNutritionPaged(user.getUserId(), pageable);

        log.info("Fetched {} foods (page {} of {}, totalElements={}) for userId={}",
                pageData.getNumberOfElements(),
                pageData.getNumber(),
                pageData.getTotalPages(),
                pageData.getTotalElements(),
                user.getUserId());

        return pageData.map(p -> {
            FoodWithNutrition f = new FoodWithNutrition();
            f.setUuid(p.getUuid().toString());
            f.setName(p.getName());
            f.setImageUrl(p.getImageUrl());
            f.setProtein(p.getProtein());
            f.setFat(p.getFat());
            f.setCalories(p.getCalories());
            f.setCarbs(p.getCarbs());
            f.setFiber(p.getFiber());
            return f;
        });
    }


    public List<FoodDTO> getFoods() {
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();

        try {

//            Not needed as user already verified
            User user = userRepository.findByEmail(userMail).orElseThrow(
                    () -> {
                        log.error("user not found");
                        return new UserNotFoundException("user not found");

                    }
            );

            log.info("Getting food Details for user {}",userMail);
            List<Food> foods = foodRepository.getAllFoodByUser(user.getUserId());

            List<FoodDTO> foodDTOList = new ArrayList<>();
            for (Food food : foods){
                FoodDTO f = new FoodDTO();
                f.setUuid(food.getUuid());
                f.setImageUrl(food.getImageUrl());
                List<NutritionDTO> n = nutritionService.getNutrition(food.getUuid());
                f.setNutritionDTOList(n);
                foodDTOList.add(f);
            }
            log.info(foods.toString());
            log.info("Food DTO : "+foodDTOList);
            return foodDTOList;
        }
        catch(Exception e){
            log.error("Some Error occurred while fetching food for a user {}", userMail);
            return null;
        }

    }

    public Page<FoodDTO> getFoodsDynamic(LocalDate startDate,
                                         LocalDate endDate,
                                         MealType mealType,
                                         String keyword,
                                         int page,
                                         int size) {

        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Specification<Food> spec = Specification
                .where(FoodSpecification.userEquals(user.getUserId()))
                .and(FoodSpecification.dateBetween(startDate, endDate))
                .and(FoodSpecification.mealTypeEquals(mealType))
                .and(FoodSpecification.notesContains(keyword));

        Page<Food> foods = foodRepository.findAll(spec, pageable);

        return foods.map(food -> {
            FoodDTO dto = new FoodDTO();
            dto.setUuid(food.getUuid());
            dto.setImageUrl(food.getImageUrl());
            dto.setNutritionDTOList(nutritionService.getNutrition(food.getUuid()));
            return dto;
        });
    }


    public List<FoodDTO> getFoods(LocalDate startDate, LocalDate endDate, MealType mealType) {
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        List<Food> foods;
        Date start = startDate != null ? Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        Date end = endDate != null ? Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        if (mealType != null) {
            foods = foodRepository.findFoodsByUserAndDateRangeAndMealType(
                    user.getUserId(),
                    start,
                    end,
                    mealType
            );
        } else {
            foods = foodRepository.findFoodsByUserAndDateRange(
                    user.getUserId(),
                    start,
                    end
            );
        }

        // Map to DTO
        List<FoodDTO> foodDTOList = new ArrayList<>();

        for (Food food : foods) {
            FoodDTO dto = new FoodDTO();
            dto.setUuid(food.getUuid());
            dto.setImageUrl(food.getImageUrl());
            dto.setNutritionDTOList(nutritionService.getNutrition(food.getUuid()));
            foodDTOList.add(dto);
        }

        return foodDTOList;
    }


    public Page<FoodDTO> getFoods(int page, int size) {

        if (size < 0 || size > 10) {
            throw new IllegalArgumentException("Page size must be greater than 0 and less than or equal to 10");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching paginated foods for user={} | page={} size={}", userMail, page, size);

        try {
            // 1. Validate user
            User user = userRepository.findByEmail(userMail)
                    .orElseThrow(() -> {
                        log.error("User not found for email={}", userMail);
                        return new UserNotFoundException("User not found");
                    });

            // 2. Build pageable
            Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
            log.debug("Pageable created: {}", pageable);

            // 3. Fetch paginated foods
            Page<Food> foodPage = foodRepository.findByUserUserId(user.getUserId(), pageable);
            log.debug("Foods fetched: totalElements={}, totalPages={}",
                    foodPage.getTotalElements(), foodPage.getTotalPages());

            // 4. Map Food -> FoodDTO
            Page<FoodDTO> dtoPage = foodPage.map(food -> {
                FoodDTO dto = new FoodDTO();
                dto.setUuid(food.getUuid());
                dto.setImageUrl(food.getImageUrl());
                dto.setNutritionDTOList(nutritionService.getNutrition(food.getUuid()));
                return dto;
            });

            log.info("Successfully returning {} food items for user={}", dtoPage.getNumberOfElements(), userMail);
            return dtoPage;

        } catch (UserNotFoundException e) {
            // Known exception
            log.error("User not found while fetching foods: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            // Unknown exception
            log.error("Unexpected error while fetching paginated foods for user={} : {}", userMail, e.getMessage(), e);
            throw new RuntimeException("Unable to fetch foods at the moment");
        }
    }




    public void deleteFood(UUID foodId) {
        try{
            log.debug("foodId to delete : {}", foodId);
            Food ft = foodRepository.getById(foodId);
            nutritionImageService.deleteImage(ft.getImageUrl());
            foodRepository.deleteById(foodId);
        }
        catch(Exception e){
            log.error("Some error occurred while deleting the food");
        }
    }

    public List<FoodWithNutrition> getTodayFoods() {
        return getFoodsByRange(
                DateUtils.startOfToday(),
                DateUtils.startOfTomorrow()
        );
    }

    public List<FoodWithNutrition> getLast7DaysFoods() {
        return getFoodsByRange(
                DateUtils.startOfLast7Days(),
                DateUtils.startOfTomorrow()
        );
    }

    public List<FoodWithNutrition> getLast30DaysFoods() {

        User user = getLoggedInUser();

        Date start = DateUtils.startOfLast30Days();
        Date end = DateUtils.startOfTomorrow();

        List<FoodWithNutritionProjection> projections =
                foodRepository.findFoodsWithNutritionByDateRange(
                        user.getUserId(), start, end
                );

        return projections.stream().map(p -> {
            FoodWithNutrition f = new FoodWithNutrition();
            f.setUuid(p.getUuid().toString());
            f.setName(p.getName());
            f.setImageUrl(p.getImageUrl());
            f.setProtein(p.getProtein());
            f.setFat(p.getFat());
            f.setCalories(p.getCalories());
            f.setCarbs(p.getCarbs());
            f.setFiber(p.getFiber());
            return f;
        }).toList();
    }

    private List<FoodWithNutrition> getFoodsByRange(Date start, Date end) {

        User user = getLoggedInUser();

        return foodRepository
                .findFoodsWithNutritionByDateRange(
                        user.getUserId(), start, end
                )
                .stream()
                .map(p -> {
                    FoodWithNutrition f = new FoodWithNutrition();
                    f.setUuid(p.getUuid().toString());
                    f.setName(p.getName());
                    f.setImageUrl(p.getImageUrl());
                    f.setProtein(p.getProtein());
                    f.setFat(p.getFat());
                    f.setCalories(p.getCalories());
                    f.setCarbs(p.getCarbs());
                    f.setFiber(p.getFiber());
                    return f;
                })
                .toList();
    }



    private User getLoggedInUser() {
        String userMail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(userMail)
                .orElseThrow(() -> {
                    log.error("User not found for email={}", userMail);
                    return new UserNotFoundException("User not found");
                });
    }


}
