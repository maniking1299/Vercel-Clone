package com.manish.vercelclone.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDeploymentRequest {
    @NotBlank
    private String gitHubUrl;
    @NotBlank
    private String command;
    @NotBlank
    private String opDir;
}
