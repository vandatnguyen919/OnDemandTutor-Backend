/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.AccountDetailsDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.service.AccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    @PutMapping("/{accountId}/become-a-tutor")
    public ResponseEntity<?> changeRoleToTutor(@PathVariable Integer accountId) {
        return accountService.changeRole(accountId, "tutor");
    }
    
    // chi cho phep account do update
    @PutMapping("/{accountId}/update-details")
    public ResponseEntity<?> updateAccountDetails(@PathVariable Integer accountId, @RequestBody AccountDetailsDto accountDetails) {
        ResponseEntity<?> response = accountService.updateAccountDetails(accountId, accountDetails);
        return response;
    }
    
    @PostMapping("/{accountId}/tutor-description")
    public ResponseEntity<?> editTutorDescription(@PathVariable Integer accountId, @RequestBody TutorDescriptionDto tutorDescriptionDto) {
        ResponseEntity<?> response = accountService.updateTutorDescription(accountId, tutorDescriptionDto);
        return response;
    }
    
    


}
