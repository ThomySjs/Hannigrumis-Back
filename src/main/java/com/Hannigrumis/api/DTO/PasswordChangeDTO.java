package com.Hannigrumis.api.DTO;

public class PasswordChangeDTO {
    private String token;
    private String password;

    public PasswordChangeDTO(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}
