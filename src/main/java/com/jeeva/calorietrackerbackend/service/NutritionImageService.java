package com.jeeva.calorietrackerbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class NutritionImageService {

    private static final Logger log = LoggerFactory.getLogger(NutritionImageService.class);

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${publicurl}")
    private String publicUrl;

    private final S3Client s3;

    public NutritionImageService(S3Client s3) {
        this.s3 = s3;
    }

    public String uploadImage(MultipartFile file, Long userID) throws IOException {

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String key = userID + "/" + fileName; // userID folder

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
        String url = getPublicUrl(key);
        return url;  // store key in DB
    }

    public String getPublicUrl(String key) {
        return publicUrl + key;
    }


    public void deleteImage(String imageUrl) {
        try {
            // imageUrl = https://mycdn.com/bucket/123/uuid-image.png
            // publicUrl = https://mycdn.com/bucket/
            String key = imageUrl.replace(publicUrl, "");

            log.info("Deleting image with key: {}", key);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3.deleteObject(deleteRequest);

            log.info("Image deleted successfully: {}", imageUrl);
        } catch (Exception e) {
            log.error("Failed to delete image: {}", imageUrl, e);
            throw e; // optional: or create a custom exception
        }
    }
}
