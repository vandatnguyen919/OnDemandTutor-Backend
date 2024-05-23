/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<?> updateAccountDetails(@PathVariable Integer accountId, @RequestBody UpdateAccountDetailsDto updateAccountDetails) {
        ResponseEntity<?> response = accountService.updateAccountDetails(accountId, updateAccountDetails);
        return response;
    }



}
