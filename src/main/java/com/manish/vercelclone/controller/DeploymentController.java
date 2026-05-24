package com.manish.vercelclone.controller;


import com.manish.vercelclone.dto.CreateDeploymentRequest;
import com.manish.vercelclone.entity.Deployment;
import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.service.DeploymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deploy")
public class DeploymentController {

    private final DeploymentService deploy;

    public DeploymentController(DeploymentService deploy) {
        this.deploy = deploy;
    }

    @PostMapping()
    public Deployment createDeploy(@RequestBody CreateDeploymentRequest req,@RequestParam Long userId){
      return deploy.createDeployment(req.getGitHubUrl(),req.getCommand(),req.getOpDir(),userId);
    }

    @GetMapping("/{id}")
    public Deployment getStatus(@PathVariable Long id){
        return deploy.getDeployment(id);
    }

    @GetMapping("/user/{userid}")
    public List<Deployment> getAllDeployment(@PathVariable Long userid){
        return deploy.allDeployments(userid);
    }
}
