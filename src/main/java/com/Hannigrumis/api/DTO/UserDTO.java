package com.Hannigrumis.api.DTO;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private boolean verified;

    public UserDTO(Long id, String name, String email, boolean verified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isVerified() {
        return verified;
    }
}
