package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.AppointmentDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.timeslot.ResponseTimeslotDto;
import com.mytutor.entities.Appointment;
import com.mytutor.entities.Timeslot;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.repositories.TimeslotRepository;
import com.mytutor.services.AppointmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        AppointmentDto dto = modelMapper.map(appointment, AppointmentDto.class);
        for (Timeslot t : appointment.getTimeslots()) {
            dto.getTimeslotIds().add(t.getId());
        }
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // pagination
    @Override
    public ResponseEntity<PaginationDto<AppointmentDto>> getAppointmentsByTutorId(Integer tutorId, AppointmentStatus status, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Appointment> appointments = appointmentRepository.findAppointmentByTutorId(tutorId, status, pageable);
        List<Appointment> listOfAppointments = appointments.getContent();

        List<AppointmentDto> content = listOfAppointments.stream()
                .map(a -> {
                    Appointment appointment = appointmentRepository.findById(a.getId())
                            .orElse(new Appointment());
                    return modelMapper.map(appointment, AppointmentDto.class);
                })
                .collect(Collectors.toList());

        PaginationDto<AppointmentDto> appointmentResponseDto = new PaginationDto<>();
        appointmentResponseDto.setContent(content);
        appointmentResponseDto.setPageNo(appointments.getNumber());
        appointmentResponseDto.setPageSize(appointments.getSize());
        appointmentResponseDto.setTotalElements(appointments.getTotalElements());
        appointmentResponseDto.setTotalPages(appointments.getTotalPages());
        appointmentResponseDto.setLast(appointments.isLast());

        return ResponseEntity.status(HttpStatus.OK).body(appointmentResponseDto);
    }

    // pagination
    @Override
    public ResponseEntity<PaginationDto<AppointmentDto>> getAppointmentsByStudentId(Integer studentId, AppointmentStatus status, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Appointment> appointments = appointmentRepository.findAppointmentByStudentId(studentId, status, pageable);
        List<Appointment> listOfAppointments = appointments.getContent();

        List<AppointmentDto> content = listOfAppointments.stream()
                .map(a -> {
                    Appointment appointment = appointmentRepository.findById(a.getId())
                            .orElse(new Appointment());
                    return modelMapper.map(appointment, AppointmentDto.class);
                })
                .collect(Collectors.toList());

        PaginationDto<AppointmentDto> appointmentResponseDto = new PaginationDto<>();
        appointmentResponseDto.setContent(content);
        appointmentResponseDto.setPageNo(appointments.getNumber());
        appointmentResponseDto.setPageSize(appointments.getSize());
        appointmentResponseDto.setTotalElements(appointments.getTotalElements());
        appointmentResponseDto.setTotalPages(appointments.getTotalPages());
        appointmentResponseDto.setLast(appointments.isLast());

        return ResponseEntity.status(HttpStatus.OK).body(appointmentResponseDto);
    }

    // student create appointment
    @Override
    public ResponseEntity<?> createAppointment(Integer studentId, AppointmentDto appointmentDto) {
        if (!Objects.equals(studentId, appointmentDto.getStudentId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot book for other student!");
        }
        appointmentDto.setCreatedAt(LocalDateTime.now());
        appointmentDto.setStatus(AppointmentStatus.PROCESSING);
        Appointment appointment = modelMapper.map(appointmentDto, Appointment.class);
        for (Integer i : appointmentDto.getTimeslotIds()) {
            Timeslot t = timeslotRepository.findById(i).get();
            if (t.isOccupied()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Cannot book because some of the timeslots is occupied.");
            }
            appointment.getTimeslots().add(t);
        }
        appointmentRepository.save(appointment);
        AppointmentDto dto = modelMapper.map(appointment, AppointmentDto.class);
        for (Timeslot t : appointment.getTimeslots()) {
            dto.getTimeslotIds().add(t.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // tutor update appointment status
    @Override
    public ResponseEntity<?> updateAppointmentStatus(Integer tutorId, Integer appointmentId, String status) {
        // confirm -> appointmentStatus = CONFIRMED + timeslot isOccupied=true
        // status of other appointment has one of the same timeslots: FAILED
        // reason for failed appointments on FE: tutor has another appointment on at least one of the timeslot you have booked.
                                        // you must contact with the tutor before booking
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if(!Objects.equals(tutorId, appointment.getTutor().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This appointment is not belong to this tutor");
        }

        if (status.toString().equalsIgnoreCase((AppointmentStatus.CONFIRMED).toString())) {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            for (Timeslot t : appointment.getTimeslots()) {
                t.setOccupied(true);
            }
            updateOtherAppointment(appointment);

        } else if (status.toString().equalsIgnoreCase((AppointmentStatus.FAILED).toString())) {
            if (!appointment.getPayments().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("This appointment has been paid, cannot set it into FAILED status");
            }
            appointment.setStatus(AppointmentStatus.FAILED);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot apply this status to the appointment");
        }

        appointmentRepository.save(appointment);

        return ResponseEntity.ok("Appointment status updated successfully");
    }

    private void updateOtherAppointment(Appointment appointment){

        List<Appointment> overlappingAppointments = appointmentRepository
                .findAppointmentsWithOverlappingTimeslots(appointment.getTimeslots(), appointment.getId());

        for (Appointment overlappingAppointment : overlappingAppointments) {
            overlappingAppointment.setStatus(AppointmentStatus.FAILED);
            timeslotRepository.deleteAll(overlappingAppointment.getTimeslots());
            appointmentRepository.save(overlappingAppointment);
        }
    }
}
