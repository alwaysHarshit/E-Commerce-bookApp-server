package org.booknest.catelogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CatelogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatelogServiceApplication.class, args);
    }

}
