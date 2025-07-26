package com.Hannigrumis.api.security;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Hannigrumis.api.user.User;
import com.Hannigrumis.api.user.UserRepository;

@Service("customUserDetails")
public class CustomUserDetails implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole())
            .build();
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        try {
            User user = userRepository.findById(id).get();
            return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole())
            .build();
        }
        catch (NoSuchElementException e) {
            throw new UsernameNotFoundException("User not found.");
        }
    }
}
