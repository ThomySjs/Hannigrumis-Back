package com.Hannigrumis.api.security;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RecoveryCode {
    @Id
    private Integer code;
    private String email;
    private Date expiration;

    public RecoveryCode() {

    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getExpiration() {
        return expiration;
    }

    public String getEmail() {
        return email;
    }

    public Integer getCode() {
        return code;
    }

    public boolean isExpired() {
        Date currentDate = new Date();
        return currentDate.after(this.expiration);
    }

}
