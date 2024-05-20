/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.service;

import com.mytutor.dto.LoginDto;
import com.mytutor.dto.RegisterDto;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Nguyen Van Dat
 */
public interface AuthService {

    ResponseEntity<?> login(LoginDto loginDto);

    ResponseEntity<?> registerAsStudent(RegisterDto registerDto);
}
