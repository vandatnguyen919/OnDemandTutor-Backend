/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto.tutor;

import lombok.Data;

/**
 *
 * @author vothimaihoa
 */
@Data
public class CertificateDto {
    
    private int id;
    
    private String certificateName;

    private String description;

    private String issuedBy;

    private Integer issuedYear;

    private String certificateUrl;

    private String subject;

    private boolean isVerified;
}
