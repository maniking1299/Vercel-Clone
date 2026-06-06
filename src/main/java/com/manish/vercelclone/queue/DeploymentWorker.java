package com.manish.vercelclone.queue;

import com.manish.vercelclone.entity.Deployment;
import com.manish.vercelclone.entity.DeploymentStatus;
import com.manish.vercelclone.repo.DeploymentRepo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
                    String tempDir = "D:/deployments/deployment-" + dp.getId();

                    dp.setStatus(DeploymentStatus.RUNNING);
                    dpRepo.save(dp);
                    log.info("Processing deployment: {}", dp.getId());

                  if( !cloneRepo(dp.getGithubUrl(),tempDir)){
                      dp.setStatus(DeploymentStatus.FAILED);
                      dpRepo.save(dp);
                      continue;
                  }

                  if(!rundockerBuild(tempDir,dp.getBuildCommand())){
                      dp.setStatus(DeploymentStatus.FAILED);
                      dpRepo.save(dp);
                      continue;
                  }

                    dp.setStatus(DeploymentStatus.SUCCESS);
                    dpRepo.save(dp);

                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    public boolean cloneRepo(String githubUrl , String directory) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder("git", "clone", githubUrl, directory);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();

        return exitCode == 0;
    }

    public boolean rundockerBuild(String directory,String buildCommand) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder("docker", "run", "--rm", "-v",
                directory + ":/app",
                "-w", "/app",
                "node:18",
                "sh", "-c", buildCommand);

        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();

        return exitCode == 0;
    }

}
