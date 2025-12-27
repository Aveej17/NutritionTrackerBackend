package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.dto.UpdateGoalRequest;
import com.jeeva.calorietrackerbackend.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;
    @GetMapping
    public ResponseEntity<?> getGoals(Authentication auth) {

        return ResponseEntity.ok(goalService.getGoals(auth));
    }

    @PutMapping
    public ResponseEntity<?> updateGoals(
        Authentication auth,
        @RequestBody UpdateGoalRequest req
    ) {
        return ResponseEntity.ok(goalService.updateGoals(auth, req));
    }
}
