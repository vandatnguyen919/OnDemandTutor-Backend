package com.mytutor.services;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.AppointmentDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.entities.Appointment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface AppointmentService {
    ResponseEntity<AppointmentDto> getAppointmentById(Integer appointmentId);
    ResponseEntity<PaginationDto<AppointmentDto>> getAppointmentsByTutorId(Integer tutorId, AppointmentStatus status, Integer pageNo, Integer pageSize);
    ResponseEntity<PaginationDto<AppointmentDto>> getAppointmentsByStudentId(Integer studentId, AppointmentStatus status, Integer pageNo, Integer pageSize);
    ResponseEntity<?> createAppointment(Integer studentId, AppointmentDto appointment);
    ResponseEntity<?> updateAppointmentStatus(Integer tutorId, Integer appointmentId, String status);
    void rollbackAppointment(Appointment appointment);
}
