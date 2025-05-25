package com.Hannigrumis.api.login;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.Hannigrumis.api.security.JwtUtils;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
public class LoginController {

    @Autowired
    private JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(path ="/user-auth")
    public ResponseEntity<?> userAuth(@RequestBody Login login) {
        try {
            Authentication authenticationRequest = 
                UsernamePasswordAuthenticationToken.unauthenticated(login.getEmail(), login.getPassword());

            Authentication authenticationResponse = 
                this.authenticationManager.authenticate(authenticationRequest);
            
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("token", jwtUtils.generateToken(login.getEmail()));
            return ResponseEntity.ok().body(hashMap);
        }
        catch (AuthenticationException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/valid-user")
    public ResponseEntity<?> checkUser() {
        System.out.println("valid");
        return ResponseEntity.ok().build();
    }
    

}
