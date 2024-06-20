/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto.auth;

import com.mytutor.constants.RegexConsts;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Nguyen Van Dat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @Email(message = "invalid email format", regexp = RegexConsts.EMAIL_REGEX)
    @NotNull
    private String email;

    @Size(message = "must be between 1 and 255 characters",
            min = 1, max = 255)
    private String fullName;

    @Pattern(message = "invalid phone number format",
            regexp = RegexConsts.PHONE_NUMBER_REGEX)
    private String phoneNumber;

//    @Pattern(message = "must be between 8 and 16 characters, including at least 1 number, 1 uppercase character, 1 lowercase character, and 1 special character",
//            regexp = RegexConsts.PASSWORD_REGEX)
    private String password;
}
