package com.manish.vercelclone.controller;

import com.manish.vercelclone.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Object health(){
        return new HealthResponse("UP","1.0");
    }
}
