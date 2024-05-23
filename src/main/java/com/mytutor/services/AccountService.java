/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.services;

import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.entities.Account;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author vothimaihoa
 */
public interface AccountService {

    public Account getAccountById(Integer accountId);

    public ResponseEntity<?> changeRole(Integer accountId, String roleName);

    public ResponseEntity<?> updateAccountDetails(Integer accountId, UpdateAccountDetailsDto updateAccountDetailsDTO);

}
