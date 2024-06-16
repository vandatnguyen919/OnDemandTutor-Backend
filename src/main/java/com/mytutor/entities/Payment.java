package com.mytutor.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

/**
 *
 * @author vothimaihoa
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private Double moneyAmount;

    @Column
    private String provider;

    @Column
    private LocalDateTime transactionTime;

    @Column
    private String transactionDate;

    @Column
    private String transactionId;

    @ManyToOne
    @JoinColumn(name="appointment_id")
    private Appointment appointment;
}
