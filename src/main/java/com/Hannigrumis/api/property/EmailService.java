package com.Hannigrumis.api.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.Hannigrumis.api.security.JwtUtils;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtUtils jwtUtils;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendConfirmationEmail(String to) {
        String token = jwtUtils.generateToken(to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirmation email.");
        message.setText(token);

        mailSender.send(message);
    }
}
