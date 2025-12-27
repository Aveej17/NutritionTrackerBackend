package com.jeeva.calorietrackerbackend.service;

import com.jeeva.calorietrackerbackend.dto.GoalResponse;
import com.jeeva.calorietrackerbackend.dto.UpdateGoalRequest;
import com.jeeva.calorietrackerbackend.model.DefaultGoals;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.model.UserGoal;
import com.jeeva.calorietrackerbackend.repository.UserGoalRepository;
import com.jeeva.calorietrackerbackend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GoalService {

    private final UserGoalRepository goalRepository;

    private final UserRepository userRepository;

    public GoalService(UserGoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public GoalResponse getGoals(Authentication auth) {

        User user = userRepository.findUserByEmail(auth.getName());

        if (user.getIsPrimeUser()) {
            return goalRepository.findByUser(user)
                    .map(goal -> new GoalResponse(
                            goal.getCalories(),
                            goal.getProtein(),
                            goal.getCarbs(),
                            goal.getFat(),
                            true
                    ))
                    .orElseGet(() -> createDefaultForPremium(user));
        }
        return new GoalResponse(
                DefaultGoals.CALORIES,
                DefaultGoals.PROTEIN,
                DefaultGoals.CARBS,
                DefaultGoals.FAT,
                false
        );
    }


    public GoalResponse updateGoals(Authentication auth, UpdateGoalRequest req) {
        User user = userRepository.findUserByEmail(auth.getName());

        if (!user.getIsPrimeUser()) {
            throw new AccessDeniedException("Upgrade required");
        }

        UserGoal goal = goalRepository
                .findByUser(user)
                .orElse(new UserGoal(user));

        goal.setCalories(req.calories());
        goal.setProtein(req.protein());
        goal.setCarbs(req.carbs());
        goal.setFat(req.fat());

        goalRepository.save(goal);

        return new GoalResponse(
                goal.getCalories(),
                goal.getProtein(),
                goal.getCarbs(),
                goal.getFat(),
                true
        );
    }
    private GoalResponse createDefaultForPremium(User user) {

        UserGoal goal = new UserGoal(user);
        goal.setCalories(DefaultGoals.CALORIES);
        goal.setProtein(DefaultGoals.PROTEIN);
        goal.setCarbs(DefaultGoals.CARBS);
        goal.setFat(DefaultGoals.FAT);

        goalRepository.save(goal);

        return new GoalResponse(
                goal.getCalories(),
                goal.getProtein(),
                goal.getCarbs(),
                goal.getFat(),
                true
        );
    }
}
