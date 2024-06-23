package com.mytutor.dto;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.timeslot.AppointmentTimeslotDto;
import com.mytutor.dto.tutor.TutorAppointmentDto;
import com.mytutor.entities.Appointment;
import com.mytutor.entities.Timeslot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vothimaihoa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAppointmentDto {
    private Integer id;

    private LocalDateTime createdAt;

    private String description;

    private String subjectName;

    private AppointmentStatus status;

    private TutorAppointmentDto tutor;

    private Integer studentId;

    private double tuition;

    private Set<AppointmentTimeslotDto> timeslots = new HashSet<>();

    public static ResponseAppointmentDto mapToDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        ResponseAppointmentDto dto = new ResponseAppointmentDto();
        dto.setId(appointment.getId());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setDescription(appointment.getDescription());
        if (appointment.getSubject() != null) {
            dto.setSubjectName(appointment.getSubject().getSubjectName());
        }
        dto.setStatus(appointment.getStatus());
        dto.setTutor(TutorAppointmentDto.mapToDto(appointment.getTutor()));
        dto.setStudentId(appointment.getStudent().getId());
        dto.setTuition(appointment.getTuition());

        convertTimeslotsToDtos(appointment, dto);

        return dto;
    }

    private static void convertTimeslotsToDtos(Appointment appointment, ResponseAppointmentDto dto) {
        for (Timeslot t : appointment.getTimeslots()) {
            dto.getTimeslots().add(AppointmentTimeslotDto.mapToDto(t));
        }
    }
}
