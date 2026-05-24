package com.manish.vercelclone.dto;


import lombok.Data;

@Data
public class CreateDeploymentRequest {
    private String gitHubUrl;
    private String command;
    private String opDir;
}
