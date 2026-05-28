package org.booknest.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.booknest.auth.dto.UpdatePasswordDTO;
import org.booknest.auth.entity.UserEntity;
import org.booknest.auth.exception.UserNotFoundException;
import org.booknest.auth.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updatePassword(UpdatePasswordDTO requestDTO, UserDetails userDetails) {

        //get the user entity from db
        String userId = userDetails.getUsername();

        UserEntity dbUser = userRepo.findById(Long.valueOf(userId)).orElseThrow(()->new UserNotFoundException("User not found"));

        //change the password
        if(requestDTO.getOldPassword()!=null && requestDTO.getNewPassword() !=null){

            //match the older password from request and db
            boolean matches = passwordEncoder.matches(requestDTO.getOldPassword(), dbUser.getPassword());

            if(!matches){
                throw new UserNotFoundException("Wrong Old Password");
            }

            //update new password
            dbUser.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        }
    }

    public UserEntity getUser(UserDetails userDetails) {
        log.debug("Getting user by username {}", userDetails.getUsername() );
        return  userRepo.findById(Long.valueOf(userDetails.getUsername())).orElseThrow(()->new UserNotFoundException("User not found"));
    }

    @Transactional
    public void deleteAccount(UserDetails userDetails) {
        userRepo.deleteById(Long.valueOf(userDetails.getUsername()));
    }
}

