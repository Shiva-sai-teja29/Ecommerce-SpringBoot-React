package com.ecommerce.ft_ecom.security.response;

import java.util.Date;
import java.util.List;

public class LoginResponse {

    private String token;
    private String username;
    private List<String> role;
    private Date expiringIn;

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

    public Date getExpiringIn() {
        return expiringIn;
    }

    public void setExpiringIn(Date expiringIn) {
        this.expiringIn = expiringIn;
    }

    public LoginResponse(String token, String username, List<String> role, Date expiringIn) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiringIn = expiringIn;
    }
}
