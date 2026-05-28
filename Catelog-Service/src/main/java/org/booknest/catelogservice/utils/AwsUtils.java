package org.booknest.catelogservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Slf4j
@Component
public class AwsUtils {

    private final S3Client s3Client;
    private final String BUCKET_NAME = "booknest-catalog";

    public AwsUtils(S3Client s3Client) {
        this.s3Client = s3Client;
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
    }


}
