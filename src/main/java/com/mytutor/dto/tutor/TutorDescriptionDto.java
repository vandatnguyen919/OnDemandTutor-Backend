/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto.tutor;

import com.mytutor.entities.Subject;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

/**
 *
 * @author vothimaihoa
 */
@Data
public class TutorDescriptionDto {
    
    private Double teachingPricePerHour;

    private String backgroundDescription;

    private String meetingLink;
    
    private String videoIntroductionLink;

    private Set<Subject> subjects = new HashSet<>();

}
