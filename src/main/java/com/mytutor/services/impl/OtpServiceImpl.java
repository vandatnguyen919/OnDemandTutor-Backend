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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiverEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);

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
