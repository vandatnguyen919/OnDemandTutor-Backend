/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.Role;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.entities.Account;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.PhoneNumberAlreadyUsedException;
import com.mytutor.repositories.AccountRepository;

import com.mytutor.services.AccountService;

import java.security.Principal;

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
    private ModelMapper modelMapper;

    @Autowired
    public AccountServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<?> changeRole(Integer accountId, String roleName) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found"));

        // Set the role to the account
        account.setRole(Role.getRole(roleName));
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
    public ResponseEntity<?> updateAccountDetails(Principal principal, Integer accountId,
                                                  UpdateAccountDetailsDto updateAccountDetailsDto) {
        Account accountDB = getAccountById(accountId);

//        if (!checkCurrentAccount(principal, accountId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this account!");
//        }

        // chỉ cập nhật khi điền vào != null và khác rỗng

        if (updateAccountDetailsDto.getDateOfBirth() != null
                && !updateAccountDetailsDto.getDateOfBirth().toString().isEmpty()) {
            accountDB.setDateOfBirth(updateAccountDetailsDto.getDateOfBirth());
        }

        if (updateAccountDetailsDto.getGender() != null
                && !updateAccountDetailsDto.getGender().toString().isEmpty()) {
            accountDB.setGender(updateAccountDetailsDto.getGender());
        }
        if (checkFilled(updateAccountDetailsDto.getAddress())) {
            accountDB.setAddress(updateAccountDetailsDto.getAddress());
        }
        if (checkFilled(updateAccountDetailsDto.getAvatarUrl())) {
            accountDB.setAvatarUrl(updateAccountDetailsDto.getAvatarUrl());
        }
        if (checkFilled(updateAccountDetailsDto.getFullName())) {
            accountDB.setFullName(updateAccountDetailsDto.getFullName());
        }

        accountRepository.save(accountDB);
        Account existedPhoneAccount = accountRepository.findByPhoneNumber(updateAccountDetailsDto.getPhoneNumber());
        if (existedPhoneAccount == null || existedPhoneAccount.getId() == accountDB.getId()) {
            accountDB.setPhoneNumber(updateAccountDetailsDto.getPhoneNumber());
            accountRepository.save(accountDB);
        } else {
            throw new PhoneNumberAlreadyUsedException("This phone number has been registered by someone else!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseAccountDetailsDto.mapToDto(accountDB));
    }

    private boolean checkFilled(String fieldName) {
        return fieldName != null && !fieldName.isBlank();
    }

    @Override
    public boolean checkCurrentAccount (Principal principal, Integer accountId) {
        Account account = accountRepository.findByEmail(principal.getName()).orElse(null);
        if (account == null) {
            return false;
        }
        return account.getId() == accountId;
    }

    public ResponseEntity<?> readAccountById(Integer id) {
        Account account = getAccountById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseAccountDetailsDto.mapToDto(account));
    }


}