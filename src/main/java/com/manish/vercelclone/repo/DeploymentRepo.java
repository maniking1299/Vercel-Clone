package com.manish.vercelclone.repo;

import com.manish.vercelclone.entity.Deployment;
import com.manish.vercelclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DeploymentRepo extends JpaRepository<Deployment,Long> {

    public List<Deployment> findByUser(User user);
}
