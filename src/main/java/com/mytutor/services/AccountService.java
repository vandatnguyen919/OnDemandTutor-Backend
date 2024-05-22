/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.service;

import com.mytutor.dto.AccountDetailsDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.entities.Account;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author vothimaihoa
 */
public interface AccountService {

    public Account getAccountById(Integer accountId);

    public ResponseEntity<?> changeRole(Integer accountId, String roleName);

    public ResponseEntity<?> updateAccountDetails(Integer accountId, AccountDetailsDto accountDetailsDTO);

    public ResponseEntity<?> updateTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto);
}
