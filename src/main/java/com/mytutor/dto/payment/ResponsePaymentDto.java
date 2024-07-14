package com.mytutor.dto.payment;

import com.mytutor.constants.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePaymentDto {
    private PaymentProvider provider;
    private String paymentUrl;
}
