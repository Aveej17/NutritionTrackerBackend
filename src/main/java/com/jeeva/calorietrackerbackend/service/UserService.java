package com.jeeva.calorietrackerbackend.service;


import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void activateSubscription(User user){
        userRepository.save(user);
    }
}
