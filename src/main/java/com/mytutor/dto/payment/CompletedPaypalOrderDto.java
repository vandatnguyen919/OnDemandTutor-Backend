package com.mytutor.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompletedPaypalOrderDto {
    private String status;
    private String payId;

    public CompletedPaypalOrderDto(String status) {
        this.status = status;
    }
}
