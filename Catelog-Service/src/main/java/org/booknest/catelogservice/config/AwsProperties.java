package org.booknest.catelogservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "aws")
@Component
@Data
public class AwsProperties {
    private String region;
    private String bucketName;
    private String accessKey;
    private String secretKey;
}