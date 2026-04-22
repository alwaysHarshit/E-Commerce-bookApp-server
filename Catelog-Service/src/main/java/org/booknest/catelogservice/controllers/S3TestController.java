package org.booknest.catelogservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

@RestController
@RequestMapping("/test")
public class S3TestController {

    private final S3Client s3Client;

    public S3TestController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping("/s3")
    public String testS3Connection() {
        try {
            ListBucketsResponse response = s3Client.listBuckets();
            return "✅ S3 Connected! Buckets found: " +
                    response.buckets().stream()
                            .map(b -> b.name())
                            .toList();
        } catch (Exception e) {
            return "❌ S3 Connection Failed: " + e.getMessage();
        }
    }
}