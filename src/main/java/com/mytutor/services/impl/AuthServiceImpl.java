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
import com.mytutor.services.AuthService;
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

import com.mytutor.constants.RoleName;
import com.mytutor.dto.IdTokenRequestDto;
import com.mytutor.utils.PasswordGenerator;

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
            AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto(token, expirationTime);

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
        account.setPassword(passwordEncoder.encode(registerDto.getPassword())); // tạo password random cho account đăng nhap bang Google

        account.setStatus(AccountStatus.ACTIVE);
        Role role = getRole(RoleName.STUDENT);
        account.setRoles(Collections.singleton(role));
        account.setCreatedAt(new Date());

        accountRepository.save(account);

        // Generate JWT after authentication succeed
        UserDetails userDetails = userDetailsService.loadUserByUsername(registerDto.getEmail());
        String token = JwtProvider.generateToken(userDetails);
        long expirationTime = JwtProvider.JWT_EXPIRATION;

        // Response ACCESS TOKEN and EXPIRATION TIME
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto(token, expirationTime);

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
    public ResponseEntity<?> loginOAuthGoogle(IdTokenRequestDto idTokenRequestDto) {
        Account account = parseIdToken(idTokenRequestDto.getIdToken());
        if (account == null) {
            return new ResponseEntity<>("id token is invalid", HttpStatus.BAD_REQUEST);
        }
        // Check user has already logged in before or new user
        account = createOrUpdateUser(account);

        // Generate JWT after authentication succeed
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getEmail());
        String token = JwtProvider.generateToken(userDetails);
        long expirationTime = JwtProvider.JWT_EXPIRATION;

        // Response ACCESS TOKEN and EXPIRATION TIME
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto(token, expirationTime);
        
        return new ResponseEntity<>(authenticationResponseDto, HttpStatus.OK);
    }

    @Transactional
    public Account createOrUpdateUser(Account account) {
        Account existingAccount = accountRepository.findByEmail(account.getEmail()).orElse(null);
        
        // User first time login with google in the application
        if (existingAccount == null) {
            Role role = getRole(RoleName.STUDENT);
            account.setPassword(passwordEncoder.encode(PasswordGenerator.generateRandomPassword(12)));
            account.setRoles(Collections.singleton(role));
            account.setCreatedAt(new Date());
            // Store user info in the database
            accountRepository.save(account);
            return account;
        }
        // Otherwise, update user info in the database
        existingAccount.setFullName(account.getFullName());
        existingAccount.setAvatarUrl(account.getAvatarUrl());
        accountRepository.save(existingAccount);
        return existingAccount;
    }

    private Account parseIdToken(String idToken) {
        try {
            // Verify token id
            GoogleIdToken idTokenObj = verifier.verify(idToken);
            
            // Extract user info payload from id token
            GoogleIdToken.Payload payload = idTokenObj.getPayload();
            String email = (String) payload.get("email");
            String fullName = (String) payload.get("name");
            String avatarUrl = (String) payload.get("picture");
            Account account = new Account();
            account.setEmail(email);
            account.setFullName(fullName);
            account.setAvatarUrl(avatarUrl);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    private Role getRole(RoleName roleName) {
        // Get a role from the database
        Role role = roleRepository.findByRoleName(roleName.name()).orElse(null);
        // Create a new role if it is not in the database
        if (role == null) {
            role = new Role();
            role.setRoleName(roleName.name());
            roleRepository.save(role);
            role = roleRepository.findByRoleName(roleName.name()).get();
        }
        return role;
    }
}

