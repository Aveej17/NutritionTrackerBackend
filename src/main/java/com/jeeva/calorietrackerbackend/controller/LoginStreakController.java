package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.dto.LoginStreakResponse;
import com.jeeva.calorietrackerbackend.exception.UserNotFoundException;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.UserRepository;
import com.jeeva.calorietrackerbackend.service.LoginStreakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login-streak")
public class LoginStreakController {

    private static final Logger log = LoggerFactory.getLogger(LoginStreakController.class);

    @Autowired
    private LoginStreakService loginStreakService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Track login and update streak for current user
     */
    @PostMapping("/track")
    public ResponseEntity<?> trackLogin() {
        log.debug("Track login endpoint called");

        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        log.error("User not found with email: {}", userEmail);
                        return new UserNotFoundException("User not found");
                    });

            LoginStreakResponse response = loginStreakService.trackLogin(user.getUserId());
            log.info("Login tracked successfully for user: {}", userEmail);
            return ResponseEntity.ok(response);

        } catch (UserNotFoundException e) {
            log.error("User not found error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");

        } catch (Exception e) {
            log.error("Error tracking login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error tracking login: " + e.getMessage());
        }
    }

    /**
     * Get login streak for current user
     */
    @GetMapping("/my-streak")
    public ResponseEntity<?> getMyLoginStreak() {
        log.debug("Get my login streak endpoint called");

        try {
            LoginStreakResponse response = loginStreakService.getMyLoginStreak();
            return ResponseEntity.ok(response);

        } catch (UserNotFoundException e) {
            log.error("User not found error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");

        } catch (RuntimeException e) {
            log.warn("No login streak data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (Exception e) {
            log.error("Error fetching login streak: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching login streak: " + e.getMessage());
        }
    }

    /**
     * Get login streak for a specific user (admin/analytics endpoint)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getLoginStreak(@PathVariable Long userId) {
        log.debug("Get login streak for userId: {}", userId);

        try {
            LoginStreakResponse response = loginStreakService.getLoginStreak(userId);
            return ResponseEntity.ok(response);

        } catch (UserNotFoundException e) {
            log.error("User not found error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");

        } catch (RuntimeException e) {
            log.warn("No login streak data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (Exception e) {
            log.error("Error fetching login streak: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching login streak: " + e.getMessage());
        }
    }
}
