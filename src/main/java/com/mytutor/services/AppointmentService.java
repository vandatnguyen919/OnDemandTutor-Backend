package com.mytutor.services;

import com.mytutor.dto.TimeSlot.InputAppointmentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface AppointmentService {
    ResponseEntity<?> createAppointment(InputAppointmentDto appointment, Integer tutorId);
}
