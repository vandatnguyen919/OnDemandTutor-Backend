/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.dto.*;
import com.mytutor.entities.Account;
import com.mytutor.entities.Role;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.jwt.JwtProvider;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.RoleRepository;
import com.mytutor.security.CustomUserDetailsService;
import com.mytutor.services.AuthService;
import jakarta.transaction.Transactional;

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
import com.mytutor.services.OtpService;
import com.mytutor.utils.PasswordGenerator;

import java.util.Collections;
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

    @Autowired
    private OtpService otpService;

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
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
        Role role = getRole(RoleName.STUDENT);
        account.setRoles(Collections.singleton(role));
        account.setCreatedAt(new Date());

        Account newAccount = accountRepository.save(account);

//        // Generate JWT after authentication succeed
//        UserDetails userDetails = userDetailsService.loadUserByUsername(registerDto.getEmail());
//        String token = JwtProvider.generateToken(userDetails);
//        long expirationTime = JwtProvider.JWT_EXPIRATION;
//
//        // Response ACCESS TOKEN and EXPIRATION TIME
//        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto(token, expirationTime);
        otpService.sendOtp(newAccount.getEmail());

        AccountResponse accountResponse = new AccountResponse(newAccount.getEmail(), "REGISTRATION");

        return ResponseEntity.status(HttpStatus.OK).body(accountResponse);

    }

    @Override
    public ResponseEntity<?> findByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
        ResponseAccountDetailsDto dto = modelMapper.map(account, ResponseAccountDetailsDto.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
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
    private Account createOrUpdateUser(Account account) {
        Account existingAccount = accountRepository.findByEmail(account.getEmail()).orElse(null);

        // User first time login with google in the application
        if (existingAccount == null) {
            Role role = getRole(RoleName.STUDENT);
            account.setPassword(passwordEncoder.encode(PasswordGenerator.generateRandomPassword(12)));
            account.setRoles(Collections.singleton(role));
            account.setCreatedAt(new Date());
            account.setStatus(AccountStatus.ACTIVE);
            // Store user info in the database
            return accountRepository.save(account);
        }
        // Otherwise, update user info in the database
        existingAccount.setFullName(account.getFullName());
        existingAccount.setAvatarUrl(account.getAvatarUrl());

        // Instead of wainting for user to enter OTP code to verify true email.
        // Using advantage of login with gg to verify true email
        if (existingAccount.getStatus() == AccountStatus.UNVERIFIED) {
            existingAccount.setStatus(AccountStatus.ACTIVE);
        }
        return accountRepository.save(existingAccount);
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

    private record AccountResponse(String email, String status) {}
}
