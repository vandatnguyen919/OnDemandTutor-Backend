package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.AppointmentDto;
import com.mytutor.dto.PaginationDto;
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

    @Override
    public ResponseEntity<PaginationDto<AppointmentDto>> getAppointmentsByTutorId(Integer tutorId, AppointmentStatus status, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Appointment> appointments = appointmentRepository.findAppointmentByTutorId(tutorId, status, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(getPaginationDto(appointments));
    }

    @Override
    public ResponseEntity<PaginationDto<AppointmentDto>> getAppointmentsByStudentId(Integer studentId, AppointmentStatus status, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Appointment> appointments = appointmentRepository.findAppointmentByStudentId(studentId, status, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(getPaginationDto(appointments));
    }

    private PaginationDto<AppointmentDto> getPaginationDto(Page<Appointment> appointments) {
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

        return appointmentResponseDto;
    }

    // student create appointment (not paid yet)
    @Override
    public ResponseEntity<?> createAppointment(Integer studentId, AppointmentDto appointmentDto) {
        appointmentDto.setCreatedAt(LocalDateTime.now());
        appointmentDto.setStatus(AppointmentStatus.PENDING_PAYMENT);
        Appointment appointment = modelMapper.map(appointmentDto, Appointment.class);
        for (Integer i : appointmentDto.getTimeslotIds()) {
            Timeslot t = timeslotRepository.findById(i).get();
            if (t.isOccupied()) {
                throw new RuntimeException("Cannot book because some timeslots are occupied!");
            }
            else {
                t.setOccupied(true);
                appointment.getTimeslots().add(t);
            }
        }
        // check coi timeslot co duoc doi occupied khong
        appointmentRepository.save(appointment);

        // response
        AppointmentDto dto = modelMapper.map(appointment, AppointmentDto.class);
        for (Timeslot t : appointment.getTimeslots()) {
            dto.getTimeslotIds().add(t.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // is called after receiving a payment status from payment system
    @Override
    public ResponseEntity<?> updatePaidAppointment(AppointmentDto appointmentDto) {
        Appointment appointment = appointmentRepository.findById(appointmentDto.getId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.PAID);
        for (Integer i : appointmentDto.getTimeslotIds()) {
            Timeslot t = timeslotRepository.findById(i).get();
            t.setAppointment(appointment);
        }
        appointmentRepository.save(appointment);
        AppointmentDto dto = modelMapper.map(appointment, AppointmentDto.class);
        for (Timeslot t : appointment.getTimeslots()) {
            dto.getTimeslotIds().add(t.getId());
        }
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }



    // tutor update appointment status: DONE or CANCELED
    @Override
    public ResponseEntity<?> updateAppointmentStatus(Integer tutorId, Integer appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if(!Objects.equals(tutorId, appointment.getTutor().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This appointment is not belong to this tutor");
        }
        if (appointment.getStatus().equals(AppointmentStatus.CANCELED)
                || appointment.getStatus().equals(AppointmentStatus.DONE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot modify status of a canceled or done appointment!");
        }

        if (status.equals((AppointmentStatus.DONE).toString())) {
            // check dieu kien chua day xong ko cho DONE
            appointment.setStatus(AppointmentStatus.DONE);
        }

        else if (status.equalsIgnoreCase((AppointmentStatus.CANCELED).toString())) {
            appointment.setStatus(AppointmentStatus.CANCELED);
            // goi service hoan tien cho student...
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This status is invalid!");
        }

        appointmentRepository.save(appointment);

        return ResponseEntity.ok("Appointment status updated successfully");
    }

    // student update appointment status (canceled)

    // ...
}
