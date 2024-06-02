package com.mytutor.services;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.AppointmentDto;
import com.mytutor.entities.Appointment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface AppointmentService {
    ResponseEntity<AppointmentDto> getAppointmentById(Integer appointmentId);
    ResponseEntity<List<AppointmentDto>> getAppointmentsByTutorId(Integer tutorId, AppointmentStatus status);
    ResponseEntity<List<AppointmentDto>> getAppointmentsByStudentId(Integer studentId, AppointmentStatus status);
    ResponseEntity<?> createAppointment(Integer studentId, AppointmentDto appointment);
    ResponseEntity<AppointmentDto> updateAppointmentStatus(Integer tutorId, Integer appointmentId, AppointmentStatus status);
}
