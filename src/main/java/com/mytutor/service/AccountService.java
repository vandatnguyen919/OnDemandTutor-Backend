/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.service;

import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.entities.Account;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

/**
 *
 * @author vothimaihoa
 */
public interface AccountService {

    Account getAccountById(Integer accountId);
    
    ResponseEntity<?> changeRole(Integer accountId, String roleName);

    
    ResponseEntity<?> updateAccountDetails(Integer accountId, UpdateAccountDetailsDto updateAccountDetailsDTO);
    
    ResponseEntity<?> updateEducation(Integer accountId, EducationDto educationDto);
    
    ResponseEntity<?> updateCertificate(Integer accountId, CertificateDto certificateDto);
    
    ResponseEntity<?> updateTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto);

    List<ResponseAccountDetailsDto> getAllAccounts();

    boolean checkCurrentAccount(Principal principal, Integer accountId);
}
