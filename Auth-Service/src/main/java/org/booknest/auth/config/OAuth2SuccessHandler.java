package org.booknest.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.booknest.auth.entity.Role;
import org.booknest.auth.entity.User;
import org.booknest.auth.repo.AuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthRepo authRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        // GitHub might not provide email if it's private, handle that
        if (email == null) {
            email = oAuth2User.getAttribute("login") + "@github.com";
        }

        Optional<User> userOptional = authRepo.findByEmail(email);
        User user;
        if (userOptional.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : (String) oAuth2User.getAttribute("login"));
            user.setProvider("GITHUB");
            user.setProviderId(oAuth2User.getName());
            user.setRole(Role.USER);
            user.setVerified(true);
            user.setCreatedAt(LocalDateTime.now());
            authRepo.save(user);
        } else {
            user = userOptional.get();
        }

        String token = jwtUtils.generateToken(user.getId(), user.getRole().name());

        // Redirect to frontend with token
        // In a real app, you might want to use a more secure way to pass the token
        response.sendRedirect("http://localhost:5173/login-success?token=" + token);
    }
}
