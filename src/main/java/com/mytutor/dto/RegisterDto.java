/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.constants.RegexConsts;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author Nguyen Van Dat
 */
@Data
public class RegisterDto {

    @Email(message = "Please enter a valid email")
    private String email;
    
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String fullName;
    
    @Pattern(regexp = RegexConsts.PHONE_NUMBER_REGEX, message = "Invalid phone number format")
    private String phoneNumber;
    
    @Pattern(regexp = RegexConsts.PASSWORD_REGEX, message = "Password must be at least 8 characters, contains both numbers and letters")
    private String password;
}
