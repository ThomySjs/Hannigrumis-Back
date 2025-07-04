package com.Hannigrumis.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.Hannigrumis.api.DTO.LoginDTO;
import com.Hannigrumis.api.DTO.PasswordDTO;
import com.Hannigrumis.api.DTO.UserDTO;
import com.Hannigrumis.api.property.EmailService;
import com.Hannigrumis.api.property.RouteService;
import com.Hannigrumis.api.security.JwtUtils;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserService {

    final private String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
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
    @Autowired
    EntityManager entityManager;
    @Autowired
    private TemplateEngine templateEngine;

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
                return buildHtmlResponse("Email is already verified.", "/images/bad_request.png", HttpStatus.BAD_REQUEST);
            }
            user.verify();
            userRepository.save(user);
            return buildHtmlResponse("Email verified.", "/images/ok.png", HttpStatus.OK);
        }
        catch (JwtException e) {
            return buildHtmlResponse("Invalid token.", "/images/bad_request.png", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> buildHtmlResponse(String message, String imagePath, HttpStatus status) {
        Context context = new Context();
        context.setVariables(Map.of(
            "message",  message,
            "image", imagePath,
            "favicon", "/images/favicon.svg"
        ));
        String htmlContent = templateEngine.process("email-verification", context);

        return ResponseEntity.status(status).contentType(MediaType.TEXT_HTML).body(htmlContent);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String createRecoveryJWT(String email) {
        return jwtUtils.generateCustomToken(email, "recovery", 90000); //1.5 mins
    }

    public ResponseEntity<?> resetPassword(String token, String password) {
        String email = jwtUtils.getEmailFronRecoveryToken(token);

        if (email == null) {
            return ResponseEntity.badRequest().body("Invalid token.");
        }
        if (password == null || password.length() < 8) {
            return ResponseEntity.badRequest().body("Invalid password");
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

    public ResponseEntity<?> getUsers(String order) {
        List<String> sortTypes = List.of("id", "name", "email", "verified");
        if (order == null || !sortTypes.contains(order)) {
            return ResponseEntity.ok(userRepository.findAll());
        }
        String query = "SELECT new com.Hannigrumis.api.DTO.UserDTO(u.id, u.name, u.email, u.verified) FROM User u ORDER BY u." + order;
        return ResponseEntity.ok(entityManager.createQuery(query, UserDTO.class).getResultList());
    }

    public ResponseEntity<?> editUser(UserDTO user) {
        if (!this.validateEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }
        try {
            User foundUser = userRepository.findById(user.getId()).get();
            if (!foundUser.getEmail().equals(user.getEmail())) {
                foundUser.setEmail(user.getEmail());
                foundUser.unverify();
            }
            foundUser.setName(user.getName());
            userRepository.save(foundUser);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(309).body("Email is already in use.");
        }
        catch (Exception e) {
            System.out.println("An error ocurred: " + e);
        }
        return ResponseEntity.ok("User updated.");
    }

    public ResponseEntity<?> deleteUser(Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted.");
    }
}
