/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.dto.AuthenticationResponseDto;
import com.mytutor.dto.LoginDto;
import com.mytutor.dto.RegisterDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Role;
import com.mytutor.jwt.JwtProvider;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.RoleRepository;
import com.mytutor.security.CustomUserDetailsService;
import org.springframework.stereotype.Service;
import com.mytutor.service.AuthService;
import java.util.Collections;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Nguyen Van Dat
 */
@Service
public class AuthtServiceImpl implements AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtProvider JwtProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> login(LoginDto loginDto) {
        try {

            // Authenticate username(email) and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT after authentication succeed
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
            String token = JwtProvider.generateToken(userDetails);
            long expirationTime = JwtProvider.JWT_EXPIRATION;

            // Response ACCESS TOKEN and EXPIRATION TIME
            AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();
            authenticationResponseDto.setAccessToken(token);
            authenticationResponseDto.setExpirationTime(expirationTime);

            return new ResponseEntity<>(authenticationResponseDto, HttpStatus.OK);
        } catch (AuthenticationException e) {

            // If Authentication failed
            return new ResponseEntity<>("Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<?> registerAsStudent(RegisterDto registerDto) {

        if (accountRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("This email has been used", HttpStatus.BAD_REQUEST);
        }
        if (accountRepository.existsByPhoneNumber(registerDto.getPhoneNumber())) {
            return new ResponseEntity<>("This phone number has been used", HttpStatus.BAD_REQUEST);
        }

        Account account = new Account();

        account.setEmail(registerDto.getEmail());
        account.setFullName(registerDto.getFullName());
        account.setPhoneNumber(registerDto.getPhoneNumber());
        account.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        account.setCreatedAt(new Date());
        account.setStatus(AccountStatus.ACTIVE);

        Role role = roleRepository.findByRoleName("student").get();
        account.setRoles(Collections.singleton(role));

        accountRepository.save(account);

        // Generate JWT after authentication succeed
        UserDetails userDetails = userDetailsService.loadUserByUsername(registerDto.getEmail());
        String token = JwtProvider.generateToken(userDetails);
        long expirationTime = JwtProvider.JWT_EXPIRATION;

        // Response ACCESS TOKEN and EXPIRATION TIME
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();
        authenticationResponseDto.setAccessToken(token);
        authenticationResponseDto.setExpirationTime(expirationTime);

        return new ResponseEntity<>(authenticationResponseDto, HttpStatus.OK);

    }

}
