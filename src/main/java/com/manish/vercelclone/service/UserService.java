package com.manish.vercelclone.service;

import com.manish.vercelclone.dto.UserRegistrationRequest;
import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {

   private final UserRepo usr ;

    public UserService(UserRepo usr) {
        this.usr = usr;
    }

    public User registerUsers(UserRegistrationRequest req){
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
       return usr.save(user);
    }
}
