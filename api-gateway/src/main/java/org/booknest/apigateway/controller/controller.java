package org.booknest.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    @GetMapping("/test")
    public ResponseEntity<String> getString() {
        return  ResponseEntity.ok("Hello World from Spring Boot api gateway");
    }
}
