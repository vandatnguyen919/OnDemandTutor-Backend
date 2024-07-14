package com.mytutor.services;

import com.mytutor.constants.PaymentProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.Principal;

public interface PaymentService {

    ResponseEntity<?> createPayment(Principal principal,
                                    HttpServletRequest req,
                                    Integer appointmentId,
                                    PaymentProvider provider);

    ResponseEntity<?> checkVNPayPayment(
            Principal principal,
            HttpServletRequest req,
            String vnp_TxnRef,
            String vnp_TransDate
    ) throws IOException;

    ResponseEntity<?> checkMomoPayment(Principal principal, String orderId);

    ResponseEntity<?> checkPaypalPayment(Principal principal, String token);

}
