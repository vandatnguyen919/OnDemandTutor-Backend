/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.AccountDetailsDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.entities.Account;
import com.mytutor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vothimaihoa
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @PutMapping("become-a-tutor/{accountId}")
    public ResponseEntity<?> changeRoleToTutor(@PathVariable Integer accountId) {
        return accountService.changeRole(accountId, "tutor");
    }
    
    // chi cho phep account do update
    @PutMapping("update-details/{accountId}")
    public ResponseEntity<?> updateAccountDetails(@PathVariable Integer accountId, @RequestBody AccountDetailsDto accountDetails) {
        ResponseEntity<?> response = accountService.updateAccountDetails(accountId, accountDetails);
        return response;
    }
    
    // chi cho phep role tutor va la tutor do update
    @PostMapping("educations/{accountId}")
    public ResponseEntity<?> editEducations(@PathVariable Integer accountId, @RequestBody EducationDto educationDto) {
        ResponseEntity<?> response = accountService.updateEducation(accountId, educationDto);
        return response;
    }
    
    @PostMapping("certificates/{accountId}")
    public ResponseEntity<?> editCertificates(@PathVariable Integer accountId, @RequestBody CertificateDto certificateDto) {
        ResponseEntity<?> response = accountService.updateCertificate(accountId, certificateDto);
        return response;
    }
    
    @PostMapping("tutor-description/{accountId}")
    public ResponseEntity<?> editTutorDescription(@PathVariable Integer accountId, @RequestBody TutorDescriptionDto tutorDescriptionDto) {
        ResponseEntity<?> response = accountService.updateTutorDescription(accountId, tutorDescriptionDto);
        return response;
    }

}
