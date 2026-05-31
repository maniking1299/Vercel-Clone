package com.manish.vercelclone.controller;

import com.manish.vercelclone.dto.UserLoginRequest;
import com.manish.vercelclone.dto.UserRegistrationRequest;
import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService usr;

    public UserController(UserService usr) {
        this.usr = usr;
    }

    @PostMapping("/register")
    public User register(@Valid @RequestBody UserRegistrationRequest user){
      return  usr.registerUsers(user);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody UserLoginRequest user){
        String email = user.getEmail();
        String pass = user.getPassword();
        return usr.login(email,pass);
    }
}
