/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.services;

import com.mytutor.dto.ForgotPasswordDto;
import com.mytutor.dto.LoginDto;
import com.mytutor.dto.RegisterDto;
import com.mytutor.dto.ResetPasswordDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

/**
 *
 * @author Nguyen Van Dat
 */
@Service
public interface AuthService {

    ResponseEntity<?> login(LoginDto loginDto);

    ResponseEntity<?> register(RegisterDto registerDto);

    ResponseEntity<?> findByEmail(String email);

    ResponseEntity<?> loginOAuthGoogle(OAuth2AuthenticationToken oAuth2AuthenticationToken);
    
    ResponseEntity<?> forgotPassword(ForgotPasswordDto forgotPasswordDto);
    
    ResponseEntity<?> resetPassword(ResetPasswordDto resetPasswordDto);
}
