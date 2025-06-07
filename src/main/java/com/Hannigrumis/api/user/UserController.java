package com.Hannigrumis.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.Hannigrumis.api.login.Login;
import com.Hannigrumis.api.login.Register;


@RestController
public class UserController {

    @Autowired
    private UserService userService;
    
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
    

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable("token") String token) {
        return userService.emailVerification(token);
    }
    

}
