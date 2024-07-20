/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.constants.Role;
import com.mytutor.dto.EmailPasswordDto;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody UpdateAccountDetailsDto updateAccountDetails) {
        return accountService.updateAccountDetails(accountId, updateAccountDetails);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> findAccountById(@PathVariable Integer accountId) {
        return accountService.readAccountById(accountId);
    }

    @GetMapping("")
    public ResponseEntity<?> findAccountsByRole(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "role", required = false) Role role
    ) {
        return accountService.getAccountsByRole(pageNo, pageSize, role);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> banAccountById(
            @PathVariable Integer accountId
    ) {
        return accountService.banAccountById(accountId);
    }

    @PostMapping("/moderators")
    public ResponseEntity<?> createModerator(@Valid @RequestBody EmailPasswordDto emailPasswordDto) {
        ResponseAccountDetailsDto responseAccount = accountService.createAccount(emailPasswordDto, Role.MODERATOR);
        return ResponseEntity.ok(responseAccount);
    }

    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody EmailPasswordDto emailPasswordDto) {
        ResponseAccountDetailsDto responseAccount = accountService.createAccount(emailPasswordDto, Role.ADMIN);
        return ResponseEntity.ok(responseAccount);
    }
}
