package com.manish.vercelclone.queue;

import com.manish.vercelclone.entity.Deployment;
import com.manish.vercelclone.entity.DeploymentStatus;
import com.manish.vercelclone.repo.DeploymentRepo;
import com.manish.vercelclone.service.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DeploymentWorker {

    private final S3Service s3Service;

    private final DeploymentQueue dpQueue;
    private final DeploymentRepo dpRepo;

    public DeploymentWorker(S3Service s3Service, DeploymentQueue dpQueue, DeploymentRepo dpRepo) {
        this.s3Service = s3Service;
        this.dpQueue = dpQueue;
        this.dpRepo = dpRepo;
    }

    @PostConstruct
    public void init(){
        new Thread(() -> {
            while(true) {
                log.info("Worker waiting for next deployment...");
                Long tkDp = null;
                Deployment dp = null;
                try {
                    tkDp = dpQueue.takeDeployment();
                    log.info("Picked up deployment id: {}", tkDp);
                    dp = dpRepo.findById(tkDp).orElse(null);
                    if (dp == null) {
                        continue;
                    }
                    String tempDir = "D:/deployments/deployment-" + dp.getId();

                    dp.setStatus(DeploymentStatus.RUNNING);
                    dpRepo.save(dp);
                    log.info("Processing deployment: {}", dp.getId());

                    if (!cloneRepo(dp.getGithubUrl(), tempDir)) {
                        log.error("Git clone failed for deployment: {}", dp.getId());
                        dp.setStatus(DeploymentStatus.FAILED);
                        dpRepo.save(dp);
                        continue;
                    }

                    if (!rundockerBuild(tempDir, dp.getBuildCommand())) {
                        log.error("Docker build failed for deployment: {}", dp.getId());
                        dp.setStatus(DeploymentStatus.FAILED);
                        dpRepo.save(dp);
                        continue;
                    }

                    log.info("Starting S3 upload for deployment: {}", dp.getId());
                    String url = s3Service.uploadDirectory(tempDir + "/" + dp.getOutputDir(), dp.getId());
                    dp.setDeployedUrl(url);

                    dp.setStatus(DeploymentStatus.SUCCESS);
                    dpRepo.save(dp);

                }  catch (Exception e) {
                log.error("Deployment failed with error: {}", e.getMessage(), e);
                try {
                    if (dp != null) {
                        dp.setStatus(DeploymentStatus.FAILED);
                        dpRepo.save(dp);
                    }
                } catch (Exception ex) {
                    log.error("Failed to update deployment status: {}", ex.getMessage());
                }
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
