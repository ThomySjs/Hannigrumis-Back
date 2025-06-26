package com.Hannigrumis.api.DTO;

public class PasswordDTO {
    private String oldPassword;
    private String newPassword;

    public PasswordDTO(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }
}
