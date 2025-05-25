package com.Hannigrumis.api.user;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class UserService {
    private UserRepository userRepository;
    private String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    @Autowired
    PasswordEncoder passwordEncoder;

    public  UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean validateEmail(String email) {
        if (!email.matches(this.emailRegex)) {
            return false;
        }
        return true;
    }

    public ResponseEntity<?> addUser(User user) {
        if (!validateEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }
        if (user.getPassword().strip().length() < 8) {
            return ResponseEntity.badRequest().body("Password must be 8+ characters long.");
        }

        User foundUser = findUserByEmail(user.getEmail());
        if (Objects.isNull(foundUser)) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            userRepository.save(user);
            return ResponseEntity.ok("User created.");
        }

        return ResponseEntity.badRequest().body("User already exists.");
    }

    public ResponseEntity<?> login(User user) {
        if (!validateEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }
        User foundUser = findUserByEmail(user.getEmail());
        if (Objects.isNull(foundUser)) {
            return ResponseEntity.notFound().build();
        }

        /* Validate password */
        
        return ResponseEntity.ok("Token");
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
