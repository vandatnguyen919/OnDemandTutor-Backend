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
public class EducationDto {

    private String majorName;
    
    private String specialization;
    
    private String universityName;
    
    private String degreeType;
    
    private Integer startYear;
    
    private Integer endYear;
    
    private String diplomaUrl;
        
    private boolean isVerified;
}
