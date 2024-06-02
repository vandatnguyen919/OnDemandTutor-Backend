/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto.tutor;

import com.mytutor.constants.DegreeType;
import lombok.Data;

/**
 *
 * @author vothimaihoa
 */
@Data
public class EducationDto {

    private int id;

    private String majorName;

    private String specialization;

    private String universityName;

    private DegreeType degreeType;

    private int startYear;

    private int endYear;

    private String diplomaUrl;

    private boolean isVerified;
}
