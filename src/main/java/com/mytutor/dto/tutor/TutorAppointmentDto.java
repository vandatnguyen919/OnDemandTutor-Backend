package com.mytutor.dto.tutor;

import com.mytutor.entities.Account;
import com.mytutor.entities.Appointment;
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
public class TutorAppointmentDto {
    private int tutorId;
    private String fullName;
    private String avatarUrl;
    private String meetingLink;

    public static TutorAppointmentDto mapToDto(Account tutor) {
        if (tutor == null) {
            return null;
        }

        TutorAppointmentDto dto = new TutorAppointmentDto();
        dto.setTutorId(tutor.getId());
        dto.setFullName(tutor.getFullName());
        dto.setAvatarUrl(tutor.getAvatarUrl());
        dto.setMeetingLink(tutor.getTutorDetail().getMeetingLink());

        return dto;
    }
}
