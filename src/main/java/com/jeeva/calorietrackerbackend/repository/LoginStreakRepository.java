package com.jeeva.calorietrackerbackend.repository;

import com.jeeva.calorietrackerbackend.model.LoginStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginStreakRepository extends JpaRepository<LoginStreak, Long> {
    
    // Find login streak by user ID
    Optional<LoginStreak> findByUserUserId(Long userId);
    
    // Check if login streak exists for user
    boolean existsByUserUserId(Long userId);
}
