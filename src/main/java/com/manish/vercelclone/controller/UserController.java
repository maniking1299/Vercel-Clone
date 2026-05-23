package com.manish.vercelclone.controller;

import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.service.UserService;
import org.springframework.stereotype.Controller;
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
    public User register(@RequestBody User user){
      return  usr.registerUsers(user);
    }
}
