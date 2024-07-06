/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.dto.tutor;

import com.mytutor.entities.Subject;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorDescriptionDto {
    
    private Double teachingPricePerHour;

    private String backgroundDescription;

    private String meetingLink;
    
    private String videoIntroductionLink;

    private Set<String> subjects = new HashSet<>();

    private String transactionAccount;

    private String transactionProvider;

    private String accountOwner;


}
