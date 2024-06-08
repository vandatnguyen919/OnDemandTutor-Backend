package com.mytutor.services;

import com.mytutor.dto.payment.RequestPaymentDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface PaymentService {

    ResponseEntity<?> createPayment(Principal principal, RequestPaymentDto requestPaymentDto);
}
