package com.jeeva.calorietrackerbackend.service;

import com.jeeva.calorietrackerbackend.dto.LoginStreakResponse;
import com.jeeva.calorietrackerbackend.exception.UserNotFoundException;
import com.jeeva.calorietrackerbackend.model.LoginStreak;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.LoginStreakRepository;
import com.jeeva.calorietrackerbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class LoginStreakService {

    private static final Logger log = LoggerFactory.getLogger(LoginStreakService.class);

    @Autowired
    private LoginStreakRepository loginStreakRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Track user login and update streak
     */
    @Transactional
    public LoginStreakResponse trackLogin(Long userId) {
        log.debug("Tracking login for userId: {}", userId);

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with userId: {}", userId);
                    return new UserNotFoundException("User not found");
                });

        // Get or create login streak record
        LoginStreak loginStreak = loginStreakRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    log.info("Creating new login streak record for userId: {}", userId);
                    LoginStreak newStreak = new LoginStreak();
                    newStreak.setUser(user);
                    newStreak.setCurrentStreak(0);
                    newStreak.setLongestStreak(0);
                    newStreak.setLastLoginDate(null);
                    return loginStreakRepository.save(newStreak);
                });

        Date today = getStartOfDay(new Date());
        Date lastLogin = loginStreak.getLastLoginDate();

        // Check if user logged in today already
        if (lastLogin != null && isSameDay(lastLogin, today)) {
            log.info("User {} already logged in today. Streak unchanged: {}", userId, loginStreak.getCurrentStreak());
            return new LoginStreakResponse(
                    loginStreak.getCurrentStreak(),
                    loginStreak.getLongestStreak(),
                    loginStreak.getLastLoginDate(),
                    "Welcome back! You already logged in today."
            );
        }

        // Check if streak continues (logged in yesterday)
        if (lastLogin != null && isYesterday(lastLogin, today)) {
            loginStreak.setCurrentStreak(loginStreak.getCurrentStreak() + 1);
            log.info("Streak continues for userId: {}. New streak: {}", userId, loginStreak.getCurrentStreak());
        } else {
            // Streak broken or first login
            loginStreak.setCurrentStreak(1);
            log.info("New streak started for userId: {}. Streak: 1", userId);
        }

        // Update longest streak if current is higher
        if (loginStreak.getCurrentStreak() > loginStreak.getLongestStreak()) {
            loginStreak.setLongestStreak(loginStreak.getCurrentStreak());
            log.info("New longest streak for userId: {}. Longest: {}", userId, loginStreak.getLongestStreak());
        }

        // Update last login date
        loginStreak.setLastLoginDate(today);

        // Save updated streak
        loginStreak = loginStreakRepository.save(loginStreak);
        log.info("Login streak updated for userId: {}. Current: {}, Longest: {}", 
                userId, loginStreak.getCurrentStreak(), loginStreak.getLongestStreak());

        return new LoginStreakResponse(
                loginStreak.getCurrentStreak(),
                loginStreak.getLongestStreak(),
                loginStreak.getLastLoginDate(),
                "Login streak updated! Current streak: " + loginStreak.getCurrentStreak() + " days"
        );
    }

    /**
     * Get login streak for current user
     */
    public LoginStreakResponse getLoginStreak(Long userId) {
        log.debug("Fetching login streak for userId: {}", userId);

        // Fetch user
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with userId: {}", userId);
                    return new UserNotFoundException("User not found");
                });

        LoginStreak loginStreak = loginStreakRepository.findByUserUserId(userId)
                .orElseThrow(() -> {
                    log.warn("No login streak record found for userId: {}", userId);
                    return new RuntimeException("No login streak data found for user");
                });

        log.info("Login streak fetched for userId: {}. Current: {}, Longest: {}", 
                userId, loginStreak.getCurrentStreak(), loginStreak.getLongestStreak());

        return new LoginStreakResponse(
                loginStreak.getCurrentStreak(),
                loginStreak.getLongestStreak(),
                loginStreak.getLastLoginDate(),
                "Login streak retrieved successfully"
        );
    }

    /**
     * Get login streak for logged-in user
     */
    public LoginStreakResponse getMyLoginStreak() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Fetching login streak for logged-in user: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userEmail);
                    return new UserNotFoundException("User not found");
                });

        return getLoginStreak(user.getUserId());
    }

    /**
     * Helper method to get start of day
     */
    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Check if two dates are the same day
     */
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Check if date1 is yesterday relative to date2
     */
    private boolean isYesterday(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        
        // Add one day to date1
        cal1.add(Calendar.DAY_OF_MONTH, 1);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
