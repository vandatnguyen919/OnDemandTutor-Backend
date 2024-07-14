/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */
@Entity
@NoArgsConstructor
@Data
@Table (name="certificate")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id")
    private Account account;

    private String certificateName;

    private String description;

    private String issuedBy;

    private Integer issuedYear;

    private String certificateUrl;

    private String subject;

    private boolean isVerified = false;

//    @Enumerated(EnumType.STRING)
//    private VerifyStatus verifyStatus = VerifyStatus.PROCESSING;
}

