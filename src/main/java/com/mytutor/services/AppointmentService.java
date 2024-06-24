package com.mytutor.services;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.InputAppointmentDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.ResponseAppointmentDto;
import com.mytutor.dto.LessonStatisticDto;
import com.mytutor.entities.Appointment;
import com.mytutor.entities.WeeklySchedule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface AppointmentService {
    ResponseEntity<ResponseAppointmentDto> getAppointmentById(Integer appointmentId);
    ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointmentsByAccountId(Integer tutorId, AppointmentStatus status, Integer pageNo, Integer pageSize);
    ResponseEntity<?> createAppointment(Integer studentId, InputAppointmentDto appointment);
    ResponseEntity<?> updateAppointmentStatus(Integer tutorId, Integer appointmentId, String status);
    ResponseEntity<?> rollbackAppointment(int appointmentId);
    void rollbackAppointment(Appointment appointment);
    ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointments(AppointmentStatus status, Integer pageNo, Integer pageSize);
    ResponseEntity<LessonStatisticDto> getStudentStatistics(Integer studentId);
    ResponseEntity<LessonStatisticDto> getTutorStatistics(Integer accountId);
    ResponseEntity<ResponseAppointmentDto> updateAppointmentSchedule(int appointmentId, int oldTimeslotId, int newWeeklyScheduleId);
}
