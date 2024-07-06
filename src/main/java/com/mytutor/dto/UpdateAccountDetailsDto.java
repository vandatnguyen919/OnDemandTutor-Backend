package com.mytutor.dto;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.RegexConsts;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;

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

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters")
    private String avatarUrl;

    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    private String fullName;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = RegexConsts.PHONE_NUMBER_REGEX, message = "Invalid phone number format")
    private String phoneNumber;

    private AccountStatus status;
}