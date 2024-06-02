package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.AppointmentDto;
import com.mytutor.dto.timeslot.ResponseTimeslotDto;
import com.mytutor.entities.Appointment;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.repositories.TimeslotRepository;
import com.mytutor.services.AppointmentService;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author vothimaihoa
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    TimeslotRepository timeslotRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseEntity<AppointmentDto> getAppointmentById(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(appointment, AppointmentDto.class));
    }

    // pagination
    @Override
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByTutorId(Integer tutorId, AppointmentStatus status) {
        return null;
    }

    // pagination
    @Override
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByStudentId(Integer studentId, AppointmentStatus status) {
        return null;
    }

    // student create appointment
    @Override
    public ResponseEntity<?> createAppointment(Integer studentId, AppointmentDto appointmentDto) {
        if (!Objects.equals(studentId, appointmentDto.getStudentId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot book for other student!");
        }
        appointmentDto.setCreatedAt(LocalDateTime.now());
        appointmentDto.setStatus(AppointmentStatus.PROCESSING);
        Appointment appointment = modelMapper.map(appointmentDto, Appointment.class);
        for (Integer i : appointmentDto.getTimeslotIds()) {
            appointment.getTimeslots().add(timeslotRepository.findById(i).get());
        }
        appointmentRepository.save(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(appointment, AppointmentDto.class));
    }

    // tutor update appointment status
    @Override
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(Integer tutorId, Integer appointmentId, AppointmentStatus status) {
        return null;
    }
}
