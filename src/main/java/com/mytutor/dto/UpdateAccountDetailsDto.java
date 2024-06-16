package com.mytutor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 * @author vothimaihoa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountDetailsDto {
    private Date dateOfBirth;
    private Boolean gender; // male: false, female: true
    private String address;
    private String avatarUrl;
    private String fullName;
    private String phoneNumber;
}