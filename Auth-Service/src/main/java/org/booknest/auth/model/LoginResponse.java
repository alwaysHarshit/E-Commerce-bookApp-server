package org.booknest.auth.model;

import lombok.Getter;
import org.booknest.auth.entity.Role;

@Getter
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private Role role;
    private String name;

    public LoginResponse(String token, String email, Role role, String name) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.name = name;
    }
}
