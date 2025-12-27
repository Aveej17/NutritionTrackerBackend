package com.jeeva.calorietrackerbackend.repository;

import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

    Optional<UserGoal> findByUser(User user);

    Optional<UserGoal> findByUser_UserId(Long userId);

    boolean existsByUser(User user);
}
