package com.manish.vercelclone.service;

import com.manish.vercelclone.dto.UserRegistrationRequest;
import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.repo.UserRepo;
import com.manish.vercelclone.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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
       return usr.save(user);
    }

    public String login(String email, String pass){

      User user =  usr.findByEmail(email);
        if(user==null){
            return null;
        }

        if(!hashing.matches(pass,user.getPassword())){
            return null;
        }

        return jwtUtil.generateToken(user.getEmail());

    }
}
