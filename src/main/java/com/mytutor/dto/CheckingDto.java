package com.mytutor.dto;

import com.mytutor.constants.VerifyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckingDto {
    private int id;
    private boolean isVerified;
    private VerifyStatus verifyStatus;
}
