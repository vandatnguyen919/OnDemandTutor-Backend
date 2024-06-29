/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.entities;

import com.mytutor.constants.DegreeType;
import com.mytutor.constants.VerifyStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */
@Entity
@NoArgsConstructor
@Data
@Table(name = "Education")
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id")
    private Account account;

    private String majorName;

    private String specialization;

    private String universityName;

    @Enumerated(EnumType.STRING)
    private DegreeType degreeType;

    private Integer startYear;

    private Integer endYear;

    private String diplomaUrl;

    private boolean isVerified = false;

//    @Enumerated(EnumType.STRING)
//    private VerifyStatus verifyStatus = VerifyStatus.PROCESSING;
}


