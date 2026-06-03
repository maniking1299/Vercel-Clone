package com.manish.vercelclone.service;

import com.manish.vercelclone.entity.Deployment;
import com.manish.vercelclone.entity.User;
import com.manish.vercelclone.queue.DeploymentQueue;
import com.manish.vercelclone.repo.DeploymentRepo;
import com.manish.vercelclone.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeploymentService {

    private final DeploymentRepo deploy;
    private final UserRepo usr;
    private final DeploymentQueue dpQueue;


    public DeploymentService(DeploymentRepo deploy, UserRepo usr, DeploymentQueue dpQueue) {
        this.deploy = deploy;
        this.usr = usr;
        this.dpQueue = dpQueue;
    }

    public Deployment createDeployment(String url, String command, String optDir, Long userId) {
        Deployment d = new Deployment();
       d.setGithubUrl(url);
       d.setBuildCommand(command);
       d.setOutputDir(optDir);
       User user = usr.findById(userId).orElse(null);
       d.setUser(user);
      Deployment saved = deploy.save(d);
      dpQueue.addDeployment(saved.getId());
      return saved;
    }

    public Deployment getDeployment(Long id){
      return deploy.findById(id).orElse(null);
    }

    public List<Deployment> allDeployments(Long userId){
        User user = usr.findById(userId).orElse(null);
       return deploy.findByUser(user);
    }
}
