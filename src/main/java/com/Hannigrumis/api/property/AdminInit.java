package com.Hannigrumis.api.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import com.Hannigrumis.api.user.User;
import com.Hannigrumis.api.user.UserService;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class AdminInit {
    @Autowired
    private UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultAdmin() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        try {
            if (userService.isEmpty()) {
                User user = new User(dotenv.get("ADMIN_ACCOUNT_NAME"), dotenv.get("ADMIN_ACCOUNT_EMAIL"), dotenv.get("ADMIN_ACCOUNT_PASSWORD"));
                user.setRole("ADMIN");
                userService.addUser(user);
            }
        }
        catch (Exception e) {
            System.out.println("An error ocurred while trying to create the default the default admin account. " + e);
        }
    }
}
