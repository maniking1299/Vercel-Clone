package com.manish.vercelclone.service;

import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {

   private final UserRepo usr ;

    public UserService(UserRepo usr) {
        this.usr = usr;
    }

    public User registerUsers(User user){
       return usr.save(user);
    }
}
