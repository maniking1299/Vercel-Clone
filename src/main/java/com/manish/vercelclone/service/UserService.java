package com.manish.vercelclone.service;

import com.manish.vercelclone.dto.UserRegistrationRequest;
import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.repo.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

   private final UserRepo usr ;
   private final BCryptPasswordEncoder hashing;

    public UserService(UserRepo usr ,BCryptPasswordEncoder hashing ) {
        this.usr = usr;
        this.hashing=hashing;
    }



    public User registerUsers(UserRegistrationRequest req){
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        String plain = req.getPassword();
        user.setPassword(hashing.encode(plain));
       return usr.save(user);
    }

    public User login(String email, String pass){

      User user =  usr.findByEmail(email);
        if(user==null){
            return null;
        }

        if(!hashing.matches(pass,user.getPassword())){
            return null;
        }

        return user;

    }
}
