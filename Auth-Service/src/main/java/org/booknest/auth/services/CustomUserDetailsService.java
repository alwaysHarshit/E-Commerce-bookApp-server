package org.booknest.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.entity.UserEntity;
import org.booknest.auth.exception.UserNotFoundException;
import org.booknest.auth.model.CustomUser;
import org.booknest.auth.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CustomUser loadUserByUsername(String email) {

        System.out.println("inside the:"+this.getClass().getName());
        UserEntity dbUserEntity = userRepo
                .findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found in db"));

        log.debug("this is the user form data base:"+ dbUserEntity);

        return new CustomUser(dbUserEntity);
    }
}
