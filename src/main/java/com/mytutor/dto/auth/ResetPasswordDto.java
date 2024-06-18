/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mytutor.dto.auth;

import com.mytutor.constants.RegexConsts;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Nguyen Van Dat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {

    @Email(message = "invalid email format", regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @NotNull
    private String email;

    @Pattern(message = "must be between 8 and 16 characters, including at least 1 number, 1 uppercase character, 1 lowercase character, and 1 special character",
            regexp = RegexConsts.PASSWORD_REGEX)
    @NotNull
    private String password;
}
