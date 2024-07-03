package com.mytutor.dto.moderator;

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
public class TutorVerificationEmailDto {
    private String email;
    private String moderatorMessage;
    private boolean isApproved;
}
