package com.vivemedellin.payloads;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    
    // Para mantener compatibilidad con requests antiguos que usen "username"
    public void setUsername(String username) {
        this.email = username;
    }
    
    public String getUsername() {
        return this.email;
    }
}
