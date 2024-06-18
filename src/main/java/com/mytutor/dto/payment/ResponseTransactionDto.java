package com.mytutor.dto.payment;

import com.mytutor.dto.AppointmentDto;
import com.mytutor.entities.Appointment;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseTransactionDto {
    private int id;

    private Double moneyAmount;

    private String provider;

    private LocalDateTime transactionTime;

    private String transactionDate;

    private String transactionId;

    private AppointmentDto appointment;
}
