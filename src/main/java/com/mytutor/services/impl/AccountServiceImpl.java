/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Role;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.RoleRepository;

import com.mytutor.services.AccountService;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author vothimaihoa
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public AccountServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<?> changeRole(Integer accountId, String roleName) {
        Role role = roleRepository.findByRoleName(roleName).get();
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));

        // Set the role to the account
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        account.setRoles(roles);
        account.setStatus(AccountStatus.PROCESSING);

        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body("Role updated successfully");
    }

    @Override
    public Account getAccountById(Integer accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    /**
     *
     * @return status code OK if updated successfully
     */
    @Override
    public ResponseEntity<?> updateAccountDetails(Principal principal, Integer accountId, UpdateAccountDetailsDto updateAccountDetailsDto) {
        Account accountDB = getAccountById(accountId);
        updateAccountDetailsDto.setFullName(accountDB.getFullName());
        updateAccountDetailsDto.setPhoneNumber(accountDB.getPhoneNumber());

        if (accountDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found!");
        }

        if (!checkCurrentAccount(principal, accountId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this account!");
        }
        modelMapper.map(updateAccountDetailsDto, accountDB);

        accountRepository.save(accountDB);

        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully!");
    }

    @Override
    public boolean checkCurrentAccount (Principal principal, Integer accountId) {
        Account account = accountRepository.findByEmail(principal.getName()).orElse(null);
        if (account == null) {
            return false;
        }
        return account.getId() == accountId;
    }

}