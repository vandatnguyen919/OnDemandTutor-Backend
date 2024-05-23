package com.mytutor.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 * @author vothimaihoa
 */
@Data
public class ResponseAccountDetailsDto {
    private Date dayOfBirth;
    private Boolean gender; // male: false, female: true
    private String address;
    private String avatarUrl;
    private String email;
    private String fullName;
    private String phoneNumber;
}
