package com.mytutor.controllers;

import com.mytutor.dto.payment.RequestPaymentDto;
import com.mytutor.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment(
            Principal principal,
            HttpServletRequest req,
            @RequestBody RequestPaymentDto requestPaymentDto
    ) {
        return paymentService.createPayment(principal, req, requestPaymentDto);
    }

    @GetMapping("/check-payment/vnpay")
    public ResponseEntity<?> checkPayment(
            HttpServletRequest req,
            @RequestParam(name = "vnp_TxnRef") String vnp_TxnRef,
            @RequestParam(name = "vnp_PayDate") String vnp_TransDate
    ) throws IOException {
        return paymentService.checkVNPayPayment(req, vnp_TxnRef, vnp_TransDate);
    }

//    @GetMapping("/refund-payment")
//    public ResponseEntity<?> refundPayment() {
//        return null;
//    }
}
