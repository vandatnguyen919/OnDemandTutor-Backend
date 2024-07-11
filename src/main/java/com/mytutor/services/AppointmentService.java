package com.mytutor.services;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.appointment.AppointmentSlotDto;
import com.mytutor.dto.appointment.InputAppointmentDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.appointment.RequestReScheduleDto;
import com.mytutor.dto.appointment.ResponseAppointmentDto;
import com.mytutor.dto.statistics.LessonStatisticDto;
import com.mytutor.entities.Appointment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author vothimaihoa
 */
@Service
public interface AppointmentService {
    ResponseAppointmentDto getAppointmentById(Integer appointmentId);

    PaginationDto<ResponseAppointmentDto> getAppointmentsByAccountId(Integer tutorId, AppointmentStatus status, Integer pageNo, Integer pageSize);

    ResponseEntity<?> createAppointment(Integer studentId, InputAppointmentDto appointment);

    ResponseEntity<?> updateAppointmentStatus(Integer tutorId, Integer appointmentId, String status);

    ResponseEntity<?> rollbackAppointment(int appointmentId);

    void rollbackAppointment(Appointment appointment);

    ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointments(AppointmentStatus status, Integer pageNo, Integer pageSize);

    ResponseEntity<LessonStatisticDto> getStudentStatistics(Integer studentId);
  
    ResponseEntity<LessonStatisticDto> getTutorStatistics(Integer accountId);
  
    ResponseEntity<ResponseAppointmentDto> updateAppointmentSchedule(int appointmentId, RequestReScheduleDto dto);
  
    ResponseEntity<AppointmentSlotDto> cancelSlotsInAppointment(int accountId, int timeslotId);

    void sendCreateBookingEmail(int appointmentId);

    double getTutorSalary(Integer accountId, Integer month, Integer year);

}
