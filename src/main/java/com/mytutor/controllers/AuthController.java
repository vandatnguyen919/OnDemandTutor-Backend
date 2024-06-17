/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.ForgotPasswordDto;
import com.mytutor.dto.LoginDto;
import com.mytutor.dto.RegisterDto;
import com.mytutor.dto.ResetPasswordDto;
import com.mytutor.services.AuthService;
import com.mytutor.services.OtpService;

import java.security.Principal;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nguyen Van Dat
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }

    @GetMapping("/callback/google/redirect")
    public ResponseEntity<?> loginWithGoogleSuccess() {
//        return authService.loginOAuthGoogle(oAuth2AuthenticationToken);
        return null;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        return authService.forgotPassword(forgotPasswordDto);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(ResetPasswordDto resetPasswordDto) {
        return authService.resetPassword(resetPasswordDto);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String receiverEmail) {
        return otpService.sendOtp(receiverEmail);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        return otpService.verifyOtp(email, otp);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        return authService.findByEmail(principal.getName());
    }
}
