package com.ecommerce.ft_ecom.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequest {

    @NotBlank
    @Size(min = 3, message = "username should be atleast 3 characters")
    private String username;

    @NotBlank
    @Size(max = 50, message = "email should not more than 50 characters")
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 50, message = "password should between 8 - 50 characters")
    private String password;

    public SignUpRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    private Set<String> roles;
}
