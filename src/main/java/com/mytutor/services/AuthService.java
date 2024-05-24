/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.services;

import com.mytutor.dto.IdTokenRequestDto;
import com.mytutor.dto.LoginDto;
import com.mytutor.dto.RegisterDto;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.entities.Account;

import java.security.Principal;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author Nguyen Van Dat
 */
@Service
public interface AuthService {

    ResponseEntity<?> login(LoginDto loginDto);

    ResponseEntity<?> register(RegisterDto registerDto);

    Optional<Account> findByEmail(String email);

    ResponseEntity<?> getAccountInfo(Principal principal, ResponseAccountDetailsDto responseAccountDetailsDto);

    Account getCurrentAccount();

    ResponseEntity<?> loginOAuthGoogle(IdTokenRequestDto idTokenRequestDto);
    
}
