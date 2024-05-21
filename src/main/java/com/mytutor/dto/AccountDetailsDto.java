/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto;

import com.mytutor.entities.Account;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author vothimaihoa
 */
@Data
@Builder
public class AccountDetailsDto {
    private Date dayOfBirth;
    private Boolean gender; // male: false, female: true
    private String address;
    private String avatarUrl;
    private String email;
    private String fullName;
    private String phoneNumber;
    
    public static final AccountDetailsDto convertToDto(Account account) {
        return AccountDetailsDto.builder()
                .email(account.getEmail())
                .fullName(account.getFullName())
                .build();
    }
}
