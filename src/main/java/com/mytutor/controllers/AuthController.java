/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import static com.mytutor.dto.AccountDetailsDto.convertToDto;
import com.mytutor.dto.IdTokenRequestDto;
import com.mytutor.dto.LoginDto;
import com.mytutor.dto.RegisterDto;
import com.mytutor.entities.Account;
import com.mytutor.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Nguyen Van Dat
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }

    @PostMapping("/login-with-google")
    public ResponseEntity loginOAuthGoogle(@RequestBody IdTokenRequestDto idTokenRequestDto) {
        return authService.loginOAuthGoogle(idTokenRequestDto);
    }
    
    @GetMapping("/profile")
    public ResponseEntity getUserInfo(Principal principal) {
        Account account = authService.findByEmail(principal.getName()).orElse(null);
        return ResponseEntity.ok().body(convertToDto(account));
    }
}