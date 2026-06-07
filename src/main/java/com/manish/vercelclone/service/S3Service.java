package com.manish.vercelclone.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class S3Service {
    @Value("${aws.accessKeyId}")
    private String aceessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String location;

    @Value("${aws.s3.bucketName}")
    private String bucketName;


    private S3Client buildS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(aceessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(location))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public String uploadDirectory(String directoryPath, Long deploymentId) throws IOException {
        S3Client s3 = buildS3Client();
        Path dirPath = Paths.get(directoryPath);

        Files.walk(dirPath)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String key = "deployments/" + deploymentId + "/" + dirPath.relativize(file).toString().replace("\\", "/");
                    s3.putObject(
                            PutObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(key)
                                    .build(),
                            RequestBody.fromFile(file)
                    );
                    log.info("Uploaded: {}", key);
                });

        return "https://" + bucketName + ".s3." + location + ".amazonaws.com/deployments/" + deploymentId;
    }
}
