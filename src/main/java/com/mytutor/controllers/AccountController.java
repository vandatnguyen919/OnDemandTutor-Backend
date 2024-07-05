/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.LessonStatisticDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.services.AccountService;
import com.mytutor.services.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public ResponseEntity<?> updateAccountDetails(
            @PathVariable Integer accountId,
            @Valid @RequestBody UpdateAccountDetailsDto updateAccountDetails,
            Principal principal) {
        return accountService.updateAccountDetails(principal, accountId, updateAccountDetails);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> findAccountById(@PathVariable Integer accountId) {
        return accountService.readAccountById(accountId);
    }

}
