package com.mytutor.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.Principal;

public interface PaymentService {

    ResponseEntity<?> createPayment(Principal principal,
                                    HttpServletRequest req,
                                    Integer appointmentId);

    ResponseEntity<?> checkVNPayPayment(
            HttpServletRequest req,
            String vnp_TxnRef,
            String vnp_TransDate
    ) throws IOException;
}
