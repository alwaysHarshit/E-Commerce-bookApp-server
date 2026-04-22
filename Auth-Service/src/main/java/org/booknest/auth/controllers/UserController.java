package org.booknest.auth.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            log.info("Fetching profile for user: {}", userDetails.getUsername());
        }
        return ResponseEntity.ok(userDetails);
    }

    @GetMapping("/gitProfile")
    public ResponseEntity<?> getGitUser(@AuthenticationPrincipal OAuth2User userDetails) {
        if (userDetails != null) {
            Object login = userDetails.getAttribute("login");
            log.info("Fetching GitHub profile for user: {}", String.valueOf(login));
        }
        return ResponseEntity.ok(userDetails);
    }
}
