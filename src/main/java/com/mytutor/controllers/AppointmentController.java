package com.mytutor.controllers;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.AppointmentDto;
import com.mytutor.entities.Appointment;
import com.mytutor.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    @Autowired
    AppointmentService appointmentService;

    // lay ra 1 appointment bat ky
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto> getAppointmentById(
            @PathVariable Integer appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    // lay ra tat ca appointment cua mot tutor theo trang thai
    @GetMapping("/tutors/{tutorId}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByTutor(
            @PathVariable Integer tutorId,
            @RequestParam AppointmentStatus status) {
        return appointmentService.getAppointmentsByTutorId(tutorId, status);
    }

    // lay ra tat ca appointment cua mot student theo trang thai
    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByStudent(
            @PathVariable Integer studentId,
            @RequestParam AppointmentStatus status) {
        return appointmentService.getAppointmentsByStudentId(studentId, status);
    }

    // student tao mot appointment moi
    @PostMapping("/students/{studentId}")
    public ResponseEntity<?> createAppointment(
            @PathVariable Integer studentId,
            @RequestBody AppointmentDto appointment
    ){
        return appointmentService.createAppointment(studentId, appointment);
    }

    // tutor xac nhan appointment
    @PutMapping("{appointmentId}/tutors/{tutorId}")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(
            @PathVariable Integer appointmentId,
            @PathVariable Integer tutorId,
            @RequestParam AppointmentStatus status
    ) {
        return appointmentService.updateAppointmentStatus(tutorId, appointmentId, status);
    }

}
