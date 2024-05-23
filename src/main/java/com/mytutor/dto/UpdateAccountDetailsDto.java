package com.mytutor.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author vothimaihoa
 */
@Data
@Builder
public class UpdateAccountDetailsDto {
    private Date dayOfBirth;
    private Boolean gender; // male: false, female: true
    private String address;
    private String avatarUrl;
}
