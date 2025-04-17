package com.ecommerce.ft_ecom.security.response;

import java.util.List;

public class LoginResponse {
    private Long id;
    private String token;
    private String username;
    private List<String> role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }


    public LoginResponse(Long id, String username, List<String> role) {
        this.id=id;
        this.username = username;
        this.role = role;
    }

    public LoginResponse(Long id, String token, String username, List<String> role) {
        this.id=id;
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
