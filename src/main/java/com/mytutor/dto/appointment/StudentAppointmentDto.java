package com.mytutor.dto.appointment;

import com.mytutor.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAppointmentDto {
    private int studentId;
    private String fullName;
    private String avatarUrl;

    public static StudentAppointmentDto mapToDto(Account student) {
        if (student == null) {
            return null;
        }

        StudentAppointmentDto dto = new StudentAppointmentDto();
        dto.setStudentId(student.getId());
        dto.setFullName(student.getFullName());
        dto.setAvatarUrl(student.getAvatarUrl());

        return dto;
    }
}
