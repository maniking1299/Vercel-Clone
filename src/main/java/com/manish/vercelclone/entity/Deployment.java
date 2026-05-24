package com.manish.vercelclone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;



@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Deployment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    User user;

    private String githubUrl;
    private String buildCommand;
    private String outputDir;
    private DeploymentStatus status = DeploymentStatus.QUEUED;
    @CreationTimestamp
    private Timestamp createdAt;
    private String deployedUrl;
}
