package org.booknest.catelogservice.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Utils {

    @Autowired
    private S3Client s3Client;

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
                .bucket("booknest-catalog")
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String bucketName;
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder()
                        .bucket("booknest-catalog")
                        .key(fileName)
                        .build())
                .toExternalForm();

    }


}
