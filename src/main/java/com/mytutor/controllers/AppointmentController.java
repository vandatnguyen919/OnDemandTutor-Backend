package com.mytutor.controllers;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.appointment.InputAppointmentDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.appointment.RequestReScheduleDto;
import com.mytutor.dto.appointment.ResponseAppointmentDto;
import com.mytutor.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getAppointmentById(appointmentId));
    }

    // lay ra tat ca appointment theo trang thai
    @GetMapping("")
    public ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointments(
            @RequestParam(value = "status", required = false) AppointmentStatus status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
        return appointmentService.getAppointments(status, pageNo, pageSize);
    }

    // lay ra tat ca appointment cua mot student theo trang thai
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointmentsByStudent(
            @PathVariable Integer accountId,
            @RequestParam(value = "status", required = false) AppointmentStatus status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getAppointmentsByAccountId(accountId, status, pageNo, pageSize));
    }

    // student tao mot appointment moi
    @PostMapping("/students/{studentId}")
    public ResponseEntity<?> createAppointment(
            @PathVariable Integer studentId,
            @RequestBody InputAppointmentDto appointment
    ){
        return appointmentService.createAppointment(studentId, appointment);
    }

    // account thay doi appointment status khi can
    @PutMapping("{appointmentId}/accounts/{accountId}")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Integer appointmentId,
            @PathVariable Integer accountId,
            @RequestParam String status
    ) {
        return appointmentService.updateAppointmentStatus(accountId, appointmentId, status);
    }

    @PutMapping("accounts/{accountId}/timeslots/{timeslotId}")
    public ResponseEntity<?> cancelSlotsInAppointment(
            @PathVariable Integer timeslotId,
            @PathVariable Integer accountId
    ) {
        return appointmentService.cancelSlotsInAppointment(accountId, timeslotId);
    }

    @DeleteMapping("{appointmentId}")
    public ResponseEntity<?> rollbackAppointment(@PathVariable Integer appointmentId) {
        return appointmentService.rollbackAppointment(appointmentId);
    }

    @PutMapping("{appointmentId}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Integer appointmentId,
            @RequestBody RequestReScheduleDto dto) {
        return appointmentService.updateAppointmentSchedule(appointmentId, dto);
    }

    @PostMapping("{appointmentId}/send-booking-email")
    public ResponseEntity<?> sendBookingEmail(
            @PathVariable Integer appointmentId
    ) {
        appointmentService.sendCreateBookingEmail(appointmentId);
        return ResponseEntity.ok().body("Email sent");
    }
}
