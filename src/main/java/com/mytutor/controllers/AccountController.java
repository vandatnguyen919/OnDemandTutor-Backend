/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.entities.Account;
import com.mytutor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    public ResponseEntity<?> changeRoleToTutor(Principal principal, @PathVariable Integer accountId) {
        if (!accountService.checkCurrentAccount(principal, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this account");
        }
        return accountService.changeRole(accountId, "tutor");
    }
    
    // chi cho phep account do update
    @PutMapping("update-details/{accountId}")
    public ResponseEntity<?> updateAccountDetails(Principal principal, @PathVariable Integer accountId, @RequestBody UpdateAccountDetailsDto accountDetails) {
        if (!accountService.checkCurrentAccount(principal, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this account");
        }
        return accountService.updateAccountDetails(accountId, accountDetails);
    }
    
    // chi cho phep role tutor va la tutor do update
    @PostMapping("educations/{accountId}")
    public ResponseEntity<?> editEducations(Principal principal, @PathVariable Integer accountId, @RequestBody EducationDto educationDto) {
        if (!accountService.checkCurrentAccount(principal, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this account");
        }
        return accountService.updateEducation(accountId, educationDto);
    }
    
    @PostMapping("certificates/{accountId}")
    public ResponseEntity<?> editCertificates(Principal principal, @PathVariable Integer accountId, @RequestBody CertificateDto certificateDto) {
        if (!accountService.checkCurrentAccount(principal, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this account");
        }
        return accountService.updateCertificate(accountId, certificateDto);
    }
    
    @PostMapping("tutor-description/{accountId}")
    public ResponseEntity<?> editTutorDescription(Principal principal, @PathVariable Integer accountId, @RequestBody TutorDescriptionDto tutorDescriptionDto) {
        if (!accountService.checkCurrentAccount(principal, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this account");
        }
        return accountService.updateTutorDescription(accountId, tutorDescriptionDto);
    }

    // only allow admin and moderator
    @GetMapping("get-all")
    public List<ResponseAccountDetailsDto> getAllAccount() {
        return accountService.getAllAccounts();
    }
    
    


}
