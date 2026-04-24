package org.booknest.catelogservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class Utils {

    @Autowired
    private S3Client s3Client;

    private final String BUCKET_NAME = "booknest-catalog";

    public String saveFileLocally(MultipartFile file) throws IOException {

        //String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileName = file.getOriginalFilename();
        Path uploadDir = Paths.get("Catelog-Service/src/main/resources/uploads/covers");

        // Create directory if it doesn't exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(fileName);
        file.transferTo(filePath);  // ← this is transferTo() you asked about

        return fileName;
    }

    public String uploadOnCloud(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String awsUrl = s3Client.utilities()
                .getUrl(GetUrlRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(fileName)
                        .build())
                .toExternalForm();

        log.info("Sucessfully uploaded file to S3");
        return awsUrl;

    }




    public void deleteFromCloud(String key) {
        try {
            // Check if file exists
            s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(key)
                            .build()
            );

            // If exists → delete
            s3Client.deleteObject(DeleteObjectRequest
                    .builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build());
            log.debug("Delete object successful");

        } catch (NoSuchKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
