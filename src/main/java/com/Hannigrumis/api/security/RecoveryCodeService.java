package com.Hannigrumis.api.security;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Hannigrumis.api.property.EmailService;
import com.Hannigrumis.api.user.UserService;

import jakarta.persistence.EntityNotFoundException;

import com.Hannigrumis.api.security.RecoveryCode;
import com.Hannigrumis.api.security.RecoveryCodeRepository;

@Service
public class RecoveryCodeService {
    @Autowired
    private RecoveryCodeRepository recoveryCodeRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    public String validateCode(Integer Code) {
        try {
            RecoveryCode foundCode = recoveryCodeRepository.getReferenceById(Code);
            if (foundCode.isExpired()) {
                return "";
            }
            return foundCode.getEmail();
        }
        catch (EntityNotFoundException e) {
            return null;
        }
    }

    public Boolean generateCode(String email) {
        if (userService.findUserByEmail(email) == null) {
            return false;
        }
        Random random = new Random();
        Integer id = 1000 + random.nextInt(9000);
        Date expDate = new Date((new Date().getTime() + 300000));
        RecoveryCode existingCode = recoveryCodeRepository.findByEmail(email);
        if (existingCode != null) {
            recoveryCodeRepository.deleteTokenByEmail(email);
        }
        RecoveryCode code = new RecoveryCode();
        code.setCode(id);
        code.setEmail(email);
        code.setExpiration(expDate);
        recoveryCodeRepository.save(code);

        emailService.sendHtmlResetCode(email, id);
        return true;
    }

    public void deleteByCode(Integer code) {
        recoveryCodeRepository.deleteById(code);
    }
}
