package com.mytutor.dto.appointment;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.entities.Appointment;
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
public class ResponseAppointmentWithoutTimeslotDto {
    private Integer id;

    private String subjectName;

    private TutorAppointmentDto tutor;

    private StudentAppointmentDto student;

    private AppointmentStatus appointmentStatus;

    private double tuition;

    public static ResponseAppointmentWithoutTimeslotDto mapToDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        ResponseAppointmentWithoutTimeslotDto dto = new ResponseAppointmentWithoutTimeslotDto();
        dto.setId(appointment.getId());
        if (appointment.getSubject() != null) {
            dto.setSubjectName(appointment.getSubject().getSubjectName());
        }
        dto.setTutor(TutorAppointmentDto.mapToDto(appointment.getTutor()));
        dto.setStudent(StudentAppointmentDto.mapToDto(appointment.getStudent()));
        dto.setTuition(appointment.getTuition());
        dto.setAppointmentStatus(appointment.getStatus());

        return dto;
    }
}
