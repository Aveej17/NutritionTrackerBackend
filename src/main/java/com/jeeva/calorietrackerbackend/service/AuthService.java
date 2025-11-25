package com.jeeva.calorietrackerbackend.service;


import com.jeeva.calorietrackerbackend.controller.AuthController;
import com.jeeva.calorietrackerbackend.dto.AuthRequest;
import com.jeeva.calorietrackerbackend.dto.UserDTO;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.UserRepository;
import com.jeeva.calorietrackerbackend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AuthService implements UserDetailsService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public User register(User user) {
        log.info("Attempting to register user with email: {}", user.getEmail());

        // Check if email already registered
        User existingUser = userRepository.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            log.warn("Registration failed: Email already registered -> {}", user.getEmail());
            throw new IllegalArgumentException("Email already registered: " + user.getEmail());
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    public String login(AuthRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not Found"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User not found with email : " +email);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public UserDTO getUserProfile(String email){
        User user = userRepository.findUserByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setUserId(user.getUserId());
        userDTO.setName(user.getName());
        return  userDTO;
    }
}
