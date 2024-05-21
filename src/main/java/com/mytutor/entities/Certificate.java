/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table (name="certificate")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private Account account;

    @Column(name = "certificate_name")
    private String certificateName;

    @Column(name = "description")
    private String description;

    @Column(name = "issued_by")
    private String issuedBy;

    @Column(name = "issued_year")
    private int issuedYear;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @Column(name = "is_verified")
    private boolean isVerified = false;
}

