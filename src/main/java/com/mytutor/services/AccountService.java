/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.services;

import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.entities.Account;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

/**
 *
 * @author vothimaihoa
 */
public interface AccountService {

    Account getAccountById(Integer accountId);

    ResponseEntity<?> changeRole(Integer accountId, String roleName);

    ResponseEntity<?> updateAccountDetails(Principal principal, Integer accountId, UpdateAccountDetailsDto updateAccountDetailsDTO);

    boolean checkCurrentAccount(Principal principal, Integer accountId);
}
