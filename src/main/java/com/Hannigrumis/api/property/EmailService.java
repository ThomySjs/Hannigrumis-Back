package com.Hannigrumis.api.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.Hannigrumis.api.security.JwtUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Async
    public void sendHtmlConfirmationEmail(String to, String baseUrl) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String token = jwtUtils.generateEmailConfirmationToken(to, 1200000); // 20 min exp time
            String fullUrl = baseUrl + "/verify?token=" + token;

            Context context = new Context();
            context.setVariable("token", fullUrl);
            String htmlContent = templateEngine.process("email-template", context);

            helper.setTo(to);
            helper.setSubject("Email de confirmación");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        }
        catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
