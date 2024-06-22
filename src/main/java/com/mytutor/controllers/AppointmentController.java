package com.mytutor.controllers;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.InputAppointmentDto;
import com.mytutor.dto.LessonStatisticDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.ResponseAppointmentDto;
import com.mytutor.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseAppointmentDto> getAppointmentById(
            @PathVariable Integer appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    // lay ra tat ca appointment theo trang thai
    @GetMapping("")
    public ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointments(
            @RequestParam(value = "status", required = false) AppointmentStatus status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
        return appointmentService.getAppointments(status, pageNo, pageSize);
    }

    // lay ra tat ca appointment cua mot tutor theo trang thai
    @GetMapping("/tutors/{tutorId}")
    public ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointmentsByTutor(
            @PathVariable Integer tutorId,
            @RequestParam(value = "status", required = false) AppointmentStatus status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
        return appointmentService.getAppointmentsByTutorId(tutorId, status, pageNo, pageSize);
    }

    // lay ra tat ca appointment cua mot student theo trang thai
    @GetMapping("/students/{studentId}")
    public ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointmentsByStudent(
            @PathVariable Integer studentId,
            @RequestParam(value = "status", required = false) AppointmentStatus status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
        return appointmentService.getAppointmentsByStudentId(studentId, status, pageNo, pageSize);
    }

    // student tao mot appointment moi
    @PostMapping("/students/{studentId}")
    public ResponseEntity<?> createAppointment(
            @PathVariable Integer studentId,
            @RequestBody InputAppointmentDto appointment
    ){
        return appointmentService.createAppointment(studentId, appointment);
    }

    // tutor thay doi appointment status khi can
    @PutMapping("{appointmentId}/tutors/{tutorId}")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Integer appointmentId,
            @PathVariable Integer tutorId,
            @RequestParam String status
    ) {
        return appointmentService.updateAppointmentStatus(tutorId, appointmentId, status);
    }

    @DeleteMapping("{appointmentId}")
    public ResponseEntity<?> rollbackAppointment(@PathVariable Integer appointmentId) {
        return appointmentService.rollbackAppointment(appointmentId);
    }
}
