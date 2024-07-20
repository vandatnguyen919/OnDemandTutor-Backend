/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.constants.RegexConsts;
import com.mytutor.dto.auth.ForgotPasswordDto;
import com.mytutor.dto.auth.LoginDto;
import com.mytutor.dto.auth.RegisterDto;
import com.mytutor.dto.auth.ResetPasswordDto;
import com.mytutor.services.AuthService;
import com.mytutor.services.OtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }

    @GetMapping("/callback/google/redirect")
    public ResponseEntity<?> loginWithGoogleSuccess() {
//        return authService.loginOAuthGoogle(oAuth2AuthenticationToken);
        return null;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        return authService.forgotPassword(forgotPasswordDto);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        return authService.resetPassword(resetPasswordDto);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(
            @RequestParam
            @Email(message = "invalid email format", regexp = RegexConsts.EMAIL_REGEX)
            String receiverEmail
    ) {
        return otpService.sendOtp(receiverEmail);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestParam
            @Email(message = "invalid email format", regexp = RegexConsts.EMAIL_REGEX)
            String email,
            @RequestParam
            @Pattern(regexp = RegexConsts.OTP_CODE_REGEX)
            String otp) {
        return otpService.verifyOtp(email, otp);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo(Principal principal) {

        return authService.findByEmail(principal.getName());
    }
}
