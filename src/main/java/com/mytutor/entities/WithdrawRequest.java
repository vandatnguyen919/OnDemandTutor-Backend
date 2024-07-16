package com.mytutor.entities;

import com.mytutor.constants.WithdrawRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author vothimaihoa
 */
@Entity
@NoArgsConstructor
@Data
@Table(name = "Withdraw_Request")
public class WithdrawRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime createdAt;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "tutor_id")
    private Account tutor;

    private String bankAccountNumber;

    private String bankAccountOwner;

    private String bankName;

    private double amount;

    private int month;

    private int year;

    @Enumerated(EnumType.STRING)
    private WithdrawRequestStatus status = WithdrawRequestStatus.PROCESSING;

}
