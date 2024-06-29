/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto.tutor;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * @author vothimaihoa
 */
@Data
public class CertificateDto {
    
    private Integer id;

    @NotBlank(message = "Certificate name must not be blank!")
    private String certificateName;

    private String description;

    private String issuedBy;

    private Integer issuedYear;

    @NotBlank(message = "Certificate URL must not be blank!")
    private String certificateUrl;

    private String subject;

    private String verifyStatus;
}
