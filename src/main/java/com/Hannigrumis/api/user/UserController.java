package com.Hannigrumis.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.Hannigrumis.api.login.Login;
import com.Hannigrumis.api.login.Register;
import com.Hannigrumis.api.security.RecoveryCodeService;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RecoveryCodeService recoveryCodeService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register userData) {
        User user = new User(userData.getName(), userData.getEmail(), userData.getPassword());
        return userService.addUser(user);
    }


    @PostMapping(path ="/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        return userService.loginSystem(login);
    }
    
    @GetMapping("/valid-user")
    public ResponseEntity<?> checkUser() {
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return userService.emailVerification(token);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> getCode(@RequestParam String email) {
        if (!userService.validateEmail(email)) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }
        if (!recoveryCodeService.generateCode(email)) {
            return ResponseEntity.badRequest().body("Email not found.");
        }
        return ResponseEntity.ok("Code sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> checkCode(@RequestParam Integer code) {
        String email = recoveryCodeService.validateCode(code);
        if (email == null) {
            return ResponseEntity.badRequest().body("Invalid code.");
        }
        recoveryCodeService.deleteByCode(code); //Deletes the token from the table after validation
        if (email.length() == 0) {
            return ResponseEntity.badRequest().body("Code has expired.");
        }
        return ResponseEntity.ok(userService.createRecoveryJWT(email));
    }
    
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String password) {
        return userService.resetPassword(token, password);
    }
}
