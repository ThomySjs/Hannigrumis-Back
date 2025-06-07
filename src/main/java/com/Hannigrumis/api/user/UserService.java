package com.Hannigrumis.api.user;

import java.util.HashMap;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Hannigrumis.api.login.Login;
import com.Hannigrumis.api.property.EmailService;
import com.Hannigrumis.api.security.JwtUtils;

import io.jsonwebtoken.JwtException;

@Component
public class UserService {

    private String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;


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
    
    public ResponseEntity<?> loginSystem(Login login) {
        try {
            Authentication authenticationRequest = 
                UsernamePasswordAuthenticationToken.unauthenticated(login.getEmail(), login.getPassword());

            Authentication authenticationResponse = 
                this.authenticationManager.authenticate(authenticationRequest);
            
            User user = userRepository.findByEmail(login.getEmail());
            if (!user.isVerified()) {
                emailService.sendConfirmationEmail(user.getEmail());
                return ResponseEntity.status(401).body("Email not verified.");
            }
            
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("token", jwtUtils.generateToken(login.getEmail()));
            return ResponseEntity.ok().body(hashMap);
        }
        catch (AuthenticationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> emailVerification(String token) {
        try {
            User user = userRepository.findByEmail(jwtUtils.getUsernameFromToken(token));
            user.verify();
            userRepository.save(user);
            return ResponseEntity.ok("Email verified.");
        }
        catch (JwtException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
