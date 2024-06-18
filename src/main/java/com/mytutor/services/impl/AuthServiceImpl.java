/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.dto.*;
import com.mytutor.entities.Account;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.security.CustomUserDetailsService;
import com.mytutor.security.SecurityUtil;
import com.mytutor.services.AuthService;

import java.net.URI;
import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import com.mytutor.constants.Role;
import com.mytutor.services.OtpService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * @author Nguyen Van Dat
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Override
    public ResponseEntity<?> login(LoginDto loginDto) {
        // Authenticate username(email) and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT after authentication succeed
        String token = securityUtil.createToken(authentication);

        // Response ACCESS TOKEN and EXPIRATION TIME
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto(token);

        return new ResponseEntity<>(authenticationResponseDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {

        if (accountRepository.existsByEmail(registerDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email has been used");
        }
        if (accountRepository.existsByPhoneNumber(registerDto.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This phone number has been used");
        }

        Account account = new Account();

        account.setEmail(registerDto.getEmail());
        account.setFullName(registerDto.getFullName());
        account.setPhoneNumber(registerDto.getPhoneNumber());
        account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        account.setStatus(AccountStatus.UNVERIFIED);
        account.setRole(Role.STUDENT);
        account.setCreatedAt(new Date());

        Account newAccount = accountRepository.save(account);

        otpService.sendOtp(newAccount.getEmail());

        AccountResponse accountResponse = new AccountResponse(newAccount.getEmail(), "REGISTRATION");

        return ResponseEntity.status(HttpStatus.OK).body(accountResponse);

    }

    @Override
    public ResponseEntity<?> findByEmail(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new AccountNotFoundException("Account not found!"));
        ResponseAccountDetailsDto dto = modelMapper.map(account, ResponseAccountDetailsDto.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> loginOAuthGoogle(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        Map<String, Object> userOAuth = oAuth2AuthenticationToken.getPrincipal().getAttributes();
        if (userOAuth == null) {
            return new ResponseEntity<>("Credentials are invalid!", HttpStatus.BAD_REQUEST);
        }
        String email = (String) userOAuth.get("email");
        String fullName = (String) userOAuth.get("name");
        String avatar = (String) userOAuth.get("picture");
        // Check user has already logged in before or new user
        Account account = accountRepository.findByEmail(email).orElse(null);

        if (account == null) {
            Account newAccount = new Account();
            newAccount.setEmail(email);
            newAccount.setFullName(fullName);
            newAccount.setAvatarUrl(avatar);
            newAccount.setStatus(AccountStatus.ACTIVE);
            newAccount.setCreatedAt(new Date());
            newAccount.setRole(Role.STUDENT);
            account = accountRepository.save(newAccount);
        }

        // Using advantage of login with gg to verify true email
        if (account.getStatus() == AccountStatus.UNVERIFIED) {
            account.setStatus(AccountStatus.ACTIVE);
        }

        if (account.getStatus().equals(AccountStatus.BANNED)) {
            return ResponseEntity.status(HttpStatus.FOUND).build();
        }

        // Generate JWT after authentication succeed
        String token = securityUtil.createToken(oAuth2AuthenticationToken);

        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto(token);

        return ResponseEntity.status(HttpStatus.FOUND).body(authenticationResponseDto);
    }

    @Override
    public ResponseEntity<?> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        String email = forgotPasswordDto.getEmail();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        otpService.sendOtp(account.getEmail());

        AccountResponse accountResponse = new AccountResponse(account.getEmail(), "FORGOT_PASSWORD");

        return ResponseEntity.status(HttpStatus.OK).body(accountResponse);
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordDto resetPasswordDto) {
        String email = resetPasswordDto.getEmail();
        String password = resetPasswordDto.getPassword();

        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setPassword(passwordEncoder.encode(password));

        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body("Reset password successfully!");
    }

    private record AccountResponse(String email, String status) {
    }
}
