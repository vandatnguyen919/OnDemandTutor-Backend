package com.mytutor.services.impl;

import com.mytutor.dto.TimeSlot.InputAppointmentDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Timeslot;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.TimeslotValidationException;
import com.mytutor.services.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
public class AppointmentServiceImpl implements AppointmentService {

    @Override
    public ResponseEntity<?> createAppointment(InputAppointmentDto appointment, Integer tutorId) {
        //
        try {


        } catch (AccountNotFoundException | TimeslotValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {

        }
        return null;
    }
}
