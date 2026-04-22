package org.booknest.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.entity.User;
import org.booknest.auth.exception.UserNotFoundException;
import org.booknest.auth.model.CustomUser;
import org.booknest.auth.repo.AuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthRepo authRepo;

    @Override
    public CustomUser loadUserByUsername(String email) {

        System.out.println("inside the:"+this.getClass().getName());
        User dbUser = authRepo
                .findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found in db"));

        log.debug("this is the user form data base:"+dbUser);

        return new CustomUser(dbUser);
    }
}
