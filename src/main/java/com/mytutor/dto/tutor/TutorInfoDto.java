/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mytutor.dto.tutor;

import com.mytutor.entities.Account;
import com.mytutor.entities.TutorDetail;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Nguyen Van Dat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorInfoDto {
    
    private int id;
    
    private Date dateOfBirth;
    
    private Boolean gender; // male: false, female: true
    
    private String address;
    
    private String avatarUrl;
    
    private String email;
    
    private String fullName;
    
    private String phoneNumber;

    private Double teachingPricePerHour;

    private String backgroundDescription;

    private String meetingLink;

    private String videoIntroductionLink;
    
    public static TutorInfoDto mapToDto (Account account, TutorDetail tutorDetail) {
        if (account == null || tutorDetail == null) {
            return null;
        }

        return TutorInfoDto.builder()
                .id(account.getId())
                .dateOfBirth(account.getDateOfBirth())
                .gender(account.getGender())
                .address(account.getAddress())
                .avatarUrl(account.getAvatarUrl())
                .email(account.getEmail())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .teachingPricePerHour(tutorDetail.getTeachingPricePerHour())
                .backgroundDescription(tutorDetail.getBackgroundDescription())
                .meetingLink(tutorDetail.getMeetingLink())
                .videoIntroductionLink(tutorDetail.getVideoIntroductionLink())
                .build();
    }
}
