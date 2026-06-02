package com.manish.vercelclone.service;

import com.manish.vercelclone.dto.UserRegistrationRequest;
import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.repo.UserRepo;
import com.manish.vercelclone.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final JwtUtil jwtUtil;

   private final UserRepo usr ;
   private final BCryptPasswordEncoder hashing;

    public UserService(JwtUtil jwtUtil, UserRepo usr , BCryptPasswordEncoder hashing ) {
        this.jwtUtil = jwtUtil;
        this.usr = usr;
        this.hashing=hashing;
    }



    public User registerUsers(UserRegistrationRequest req){
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        String plain = req.getPassword();
        user.setPassword(hashing.encode(plain));
       log.info("User Registered SucessFully");
       return usr.save(user);
    }

    public String login(String email, String pass){

      User user =  usr.findByEmail(email);
        if(user==null){
            log.warn("No User Found");
            return null;
        }

        if(!hashing.matches(pass,user.getPassword())){
            log.warn("Incorrect Password");
            return null;
        }

        log.info("Login Sucessfull");
        return jwtUtil.generateToken(user.getEmail());

    }
}
