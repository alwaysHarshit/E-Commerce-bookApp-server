package org.booknest.auth.model;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
