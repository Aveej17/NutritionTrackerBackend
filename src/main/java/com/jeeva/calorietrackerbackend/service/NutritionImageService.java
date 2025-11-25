package com.jeeva.calorietrackerbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class NutritionImageService {

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    private final S3Client s3;

    public NutritionImageService(S3Client s3) {
        this.s3 = s3;
    }

    public String uploadUserImage(MultipartFile file, Long userID) throws IOException {

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String key = userID + "/" + fileName; // userID folder

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        return key;  // store key in DB
    }

}
