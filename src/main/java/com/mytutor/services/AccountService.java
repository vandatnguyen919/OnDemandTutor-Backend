/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.services;

import com.mytutor.constants.Role;
import com.mytutor.dto.EmailPasswordDto;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.dto.auth.LoginDto;
import com.mytutor.entities.Account;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

/**
 *
 * @author vothimaihoa
 */
public interface AccountService {

    ResponseEntity<?> getAccountsByRole(Integer pageNo, Integer pageSize, Role role);

    ResponseEntity<?> banAccountById(Integer accountId);

    Account getAccountById(Integer accountId);

    ResponseEntity<?> changeRole(Integer accountId, String roleName);

    ResponseEntity<?> updateAccountDetails(Integer accountId, UpdateAccountDetailsDto updateAccountDetailsDTO);

    boolean checkCurrentAccount(Principal principal, Integer accountId);

    ResponseEntity<?> readAccountById(Integer accountId);

    ResponseAccountDetailsDto createAccount(EmailPasswordDto emailPasswordDto, Role role);
}
