package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tracker/user")
public class UserController {

    @Autowired
    private UserService userService;

}
