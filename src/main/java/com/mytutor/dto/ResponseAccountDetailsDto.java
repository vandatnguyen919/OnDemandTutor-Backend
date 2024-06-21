/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.constants.RegexConsts;
import com.mytutor.constants.Role;
import com.mytutor.entities.Account;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseAccountDetailsDto {

    private int id;
    private String dateOfBirth;
    private String gender; // male: false, female: true
    private String address;
    private String avatarUrl;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String status;
    private Role role;
    private String createAt;

    public static ResponseAccountDetailsDto mapToDto(Account account) {
        if (account == null) {
            return null;
        }

        return ResponseAccountDetailsDto.builder()
                .id(account.getId())
                .dateOfBirth(new SimpleDateFormat("yyyy-MM-dd").format(account.getDateOfBirth()))
                .gender(account.getGender() ? "female" : "male")
                .address(account.getAddress())
                .avatarUrl(account.getAvatarUrl())
                .email(account.getEmail())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .status(account.getStatus().toString())
                .role(account.getRole())
                .createAt(RegexConsts.sdf.format(account.getCreatedAt()))
                .build();
    }

}