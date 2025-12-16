package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.dto.AuthRequest;
import com.jeeva.calorietrackerbackend.dto.AuthResponse;
import com.jeeva.calorietrackerbackend.exception.ErrorResponse;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final org.slf4j.Logger log= org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        log.info("Register request received for email: {}", user.getEmail());

        try {
            User savedUser = authService.register(user);
            log.info(" User registered successfully: {}", savedUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body("User Created Successfully");
        } catch (IllegalArgumentException e) {
            log.warn(" Registration failed - {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), System.currentTimeMillis()));
        } catch (Exception e) {
            log.error(" Unexpected error during registration for {}: {}", user.getEmail(), e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), System.currentTimeMillis()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        log.info("Login request received for: {}", request.getEmail());
        try {
            log.info("Calling login Service ");
            AuthResponse response = authService.login(request);
            log.info(" Login successful for: {}", request.getEmail());

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (IllegalArgumentException e) {
            log.warn(" Login failed for {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), System.currentTimeMillis()));
        }
        catch (UsernameNotFoundException e) {
            log.warn("No account found for {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "No account found with this email",
                            HttpStatus.NOT_FOUND.value(),
                            System.currentTimeMillis()
                    ));
        }
        catch (Exception e) {
            log.error(" Unexpected error during login for {}: {}", request.getEmail(), e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), System.currentTimeMillis()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully")
        );
    }

}
