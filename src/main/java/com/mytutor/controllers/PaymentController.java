package com.mytutor.controllers;

import com.mytutor.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment() {
        return null;
    }

    @GetMapping("/check-payment")
    public ResponseEntity<?> checkPayment() {
        return null;
    }

//    @GetMapping("/refund-payment")
//    public ResponseEntity<?> refundPayment() {
//        return null;
//    }
}
