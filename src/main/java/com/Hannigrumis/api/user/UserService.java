package com.Hannigrumis.api.user;

import java.util.HashMap;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Hannigrumis.api.DTO.LoginDTO;
import com.Hannigrumis.api.DTO.PasswordDTO;
import com.Hannigrumis.api.DTO.UserDTO;
import com.Hannigrumis.api.property.EmailService;
import com.Hannigrumis.api.property.RouteService;
import com.Hannigrumis.api.security.JwtUtils;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserService {

    private String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RouteService routeService;

    public boolean isEmpty() {
        return userRepository.count() < 1;
    }

    public boolean validateEmail(String email) {
        return email.matches(this.emailRegex);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean validPasswordLenght(String password) {
        return password.strip().length() >= 8;
    }

    public ResponseEntity<?> addUser(User user) {
        if (!validateEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }
        if (!this.validPasswordLenght(user.getPassword())) {
            return ResponseEntity.badRequest().body("Password must be 8+ characters long.");
        }

        User foundUser = findUserByEmail(user.getEmail());
        if (Objects.isNull(foundUser)) {;
            user.setPassword(this.encodePassword(user.getPassword()));

            userRepository.save(user);
            return ResponseEntity.ok("User created.");
        }

        return ResponseEntity.badRequest().body("User already exists.");
    }

    public ResponseEntity<?> loginSystem(LoginDTO login) {
        try {
            Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(login.getEmail(), login.getPassword());

            Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);

            User user = userRepository.findByEmail(login.getEmail());
            if (!user.isVerified()) {
                emailService.sendHtmlConfirmationEmail(user.getEmail(), routeService.getAppUrl());
                return ResponseEntity.status(401).body("Email not verified.");
            }

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("token", jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole()));
            return ResponseEntity.ok().body(hashMap);
        }
        catch (AuthenticationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> emailVerification(String token) {
        try {
            User user = userRepository.findByEmail(jwtUtils.getUsernameFromToken(token));
            if (user.isVerified()) {
                return ResponseEntity.badRequest().body("Email is already verified.");
            }
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

    public String createRecoveryJWT(String email) {
        return jwtUtils.generateCustomToken(email, "recovery", 90000); //1.5 mins
    }

    public ResponseEntity<?> resetPassword(String token, String password) {
        String email = jwtUtils.getEmailFronRecoveryToken(token);
        System.out.println(email);

        if (email == null) {
            return ResponseEntity.badRequest().body("Invalid token.");
        }
        User user = userRepository.findByEmail(email);
        user.setPassword(this.encodePassword(password));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated.");
    }

    public ResponseEntity<?> changePassword(HttpServletRequest request, PasswordDTO password){
        if (!this.validPasswordLenght(password.getNewPassword())) {
            return ResponseEntity.badRequest().body("New password must be 8+ characters long.");
        }
        String token = jwtUtils.parseJwt(request);
        String email = jwtUtils.getUsernameFromToken(token);

        User user = userRepository.findByEmail(email);
        if (!passwordEncoder.matches(password.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Password dont match.");
        }
        user.setPassword(this.encodePassword(password.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated");
    }

    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userRepository.getAllUsers());
    }

    public ResponseEntity<?> editUser(UserDTO user) {
        User foundUser = userRepository.getReferenceById(user.getId());
        if (!this.validateEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }
        if(foundUser.equals(null)) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        if (foundUser.getEmail() != user.getEmail()) {
            foundUser.setEmail(user.getEmail());
            foundUser.unverify();
        }
        foundUser.setName(user.getName());
        try {
            userRepository.save(foundUser);
            return ResponseEntity.ok("User updated.");
        }
        catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(309).body("Email is already in use.");
        }
    }

    public ResponseEntity<?> deleteUser(Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted.");
    }
}
