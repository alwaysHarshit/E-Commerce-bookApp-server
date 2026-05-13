package org.booknest.auth.utils;

import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.entity.UserEntity;
import org.booknest.auth.exception.UserNotFoundException;
import org.booknest.auth.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public CustomUser loadUserByUsername(String usernameOrId) {
        log.debug("Loading user by identifier: {}", usernameOrId);

        UserEntity dbUserEntity;
        try {
            // Try parsing as ID first (for JWT validation)
            long userId = Long.parseLong(usernameOrId);
            dbUserEntity = userRepo.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        } catch (NumberFormatException e) {
            // If not a number, treat as email (for initial login)
            dbUserEntity = userRepo.findByEmail(usernameOrId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + usernameOrId));
        }

        return new CustomUser(dbUserEntity);
    }
}
