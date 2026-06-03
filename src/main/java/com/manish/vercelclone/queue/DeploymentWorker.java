package com.manish.vercelclone.queue;

import com.manish.vercelclone.entity.Deployment;
import com.manish.vercelclone.entity.DeploymentStatus;
import com.manish.vercelclone.repo.DeploymentRepo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeploymentWorker {

    private final DeploymentQueue dpQueue;
    private final DeploymentRepo dpRepo;

    public DeploymentWorker(DeploymentQueue dpQueue,DeploymentRepo dpRepo) {
        this.dpQueue = dpQueue;
        this.dpRepo = dpRepo;
    }

    @PostConstruct
    public void init(){
        new Thread(() -> {
            while(true) {
                try{
                    Long tkDp=  dpQueue.takeDeployment();
                    Deployment dp =  dpRepo.findById(tkDp).orElse(null);
                    if(dp == null){
                        continue;
                    }

                    dp.setStatus(DeploymentStatus.RUNNING);
                    dpRepo.save(dp);
                    log.info("Processing deployment: {}", dp.getId());

                    dp.setStatus(DeploymentStatus.SUCCESS);
                    dpRepo.save(dp);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

}
