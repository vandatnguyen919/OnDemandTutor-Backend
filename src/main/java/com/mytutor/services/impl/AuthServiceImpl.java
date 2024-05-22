/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.dto.*;
import com.mytutor.entities.Account;
import com.mytutor.entities.Role;
import com.mytutor.jwt.JwtProvider;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.RoleRepository;
import com.mytutor.security.CustomUserDetailsService;
import com.mytutor.service.AuthService;
import jakarta.transaction.Transactional;

import java.security.Principal;
import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;


import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Nguyen Van Dat
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtProvider JwtProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final GoogleIdTokenVerifier verifier;

    public AuthServiceImpl(@Value("${app.googleClientId}") String clientId, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        NetHttpTransport transport = new NetHttpTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

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
    public ResponseEntity<?> register(RegisterDto registerDto) {

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

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public ResponseEntity<?> getAccountInfo(Principal principal, ResponseAccountDetailsDto responseAccountDetailsDto) {
        Account account = findByEmail(principal.getName()).orElse(null);
        return ResponseEntity.ok().body(modelMapper.map(account, ResponseAccountDetailsDto.class));
    }

    @Override
    public String loginOAuthGoogle(IdTokenRequestDto requestBody) {
        Account account = verifyIDToken(requestBody.getIdToken());
        System.out.println(requestBody.getIdToken());
        System.out.println(account.getEmail());
        if (account == null) {
            throw new IllegalArgumentException();
        }
        account = createOrUpdateUser(account);
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getEmail());
        String token = JwtProvider.generateToken(userDetails);
        return token;
    }

    @Transactional
    public Account createOrUpdateUser(Account account) {
        Account existingAccount = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (existingAccount == null) {
            Role userRole = new Role();
            Role role = roleRepository.findByRoleName("student").get();
            account.setRoles(Collections.singleton(role));
            account.setPassword("123"); // phải có password vì nếu null sẽ bị exception trong hàm User của CustomerUserDetails trong spring security
            accountRepository.save(account);
            return account;
        }
//        existingAccount.setFullName(account.getFullName());
//        existingAccount.setPhoneNumber(account.getPhoneNumber());
//        existingAccount.setAvatarUrl(account.getAvatarUrl());
        accountRepository.save(existingAccount);
        return existingAccount;
    }

    private Account verifyIDToken(String idToken) {
        try {
            GoogleIdToken idTokenObj = verifier.verify(idToken);
            if (idTokenObj == null) {
                return null;
            }
            GoogleIdToken.Payload payload = idTokenObj.getPayload();
            String email = (String) payload.get("email");
            String fullName = (String) payload.get("full_name");
            String phoneNumber = (String) payload.get("phone_number");
            String avatarUrl = (String) payload.get("avatar_url");
            Account account = new Account();
            account.setEmail(email);
            account.setFullName(fullName);
            account.setPhoneNumber(phoneNumber);
            account.setAvatarUrl(avatarUrl);
            return account;
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }
    }

}
