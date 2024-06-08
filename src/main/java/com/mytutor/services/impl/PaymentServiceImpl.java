package com.mytutor.services.impl;

import com.mytutor.dto.AppointmentDto;
import com.mytutor.dto.payment.RequestPaymentDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Appointment;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.AppointmentNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public ResponseEntity<?> createPayment(Principal principal, RequestPaymentDto requestPaymentDto) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT cannot be found or trusted");
        }
        Account payer = accountRepository.findByEmail(principal.getName()).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Appointment appointment = appointmentRepository.findById(requestPaymentDto.getAppointmentId()).orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));

        return null;
    }
}
