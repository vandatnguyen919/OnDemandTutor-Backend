/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.entities.Account;
import com.mytutor.entities.Otp;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.OtpRepository;
import com.mytutor.services.OtpService;
import com.mytutor.utils.PasswordGenerator;
import java.time.LocalDateTime;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author Nguyen Van Dat
 */
@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final long EXPIRATION_TIME = 5; // 5 minutes 

    @Override
    public ResponseEntity<?> sendOtp(String receiverEmail) {

        // Generate a random OTP code
        String otp = PasswordGenerator.generateRandomOtpCode(6);

        // Save it to the database or update if it resends
        Account account = accountRepository.findByEmail(receiverEmail).orElseThrow(() -> new AccountNotFoundException("Email not registered yet"));

        Otp otpEntity = new Otp();
        otpEntity.setEmail(receiverEmail);
        otpEntity.setCode(otp);
        otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(EXPIRATION_TIME));
        otpEntity.setAccount(account);

        otpRepository.save(otpEntity);

        // Send otp to the receiver
        String content = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>OTP Verification</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f3f2f7;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            margin: 5px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1) !important; \n" +
                "        }\n" +
                "        .header {\n" +
                "            background: linear-gradient(90deg, #672DEF 0%, #FA6EAD 100%);\n" +
                "            color: #ffffff;\n" +
                "            padding: 10px 0;\n" +
                "            text-align: center;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .otp {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "            color: #672DEF;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            margin-top: 20px;\n" +
                "            text-align: center;\n" +
                "            color: #777;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            margin-top: 10px;\n" +
                "            font-size: 16px;\n" +
                "            color: #ffffff !important;\n" +
                "            background: linear-gradient(90deg, #672DEF 0%, #FA6EAD 100%);\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>OTP Verification</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear User,</p>\n" +
                "            <p>Thank you for signing up. To complete your registration, please use the following One Time Password (OTP):</p>\n" +
                "            <p class=\"otp\">" + otp +"</p>\n" +
                "            <p>This OTP is valid for the next " + EXPIRATION_TIME + " minutes. Please do not share this OTP with anyone.</p>\n" +
                "            <p>If you did not request this, please ignore this email.</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2024 My Tutor. All rights reserved.</p>\n" +
                "            <p><a href=\"http://localhost:5173\" class=\"button\">Visit Our Website</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(receiverEmail);
            helper.setSubject("Your OTP Code");
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);
        
        return ResponseEntity.status(HttpStatus.OK).body(receiverEmail);
    }

    @Override
    public ResponseEntity<?> verifyOtp(String email, String otp) {
        Otp otpEntity = otpRepository.findById(email).orElse(null);
        if (otpEntity == null || otpEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This OTP is expired or not found");
        }

        if (!otpEntity.getCode().equals(otp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This OTP is not correct");
        }

        Account account = otpEntity.getAccount();
        if (account.getStatus() == AccountStatus.UNVERIFIED) {
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);
        }
        //invalidate OTP after verification succeed
        otpEntity.setExpirationTime(LocalDateTime.now().minusMinutes(EXPIRATION_TIME));
        otpRepository.save(otpEntity);
        
        return ResponseEntity.status(HttpStatus.OK).body("OTP verification succeed");
    }
}
