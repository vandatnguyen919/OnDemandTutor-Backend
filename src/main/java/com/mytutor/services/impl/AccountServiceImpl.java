/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.dto.AccountDetailsDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Role;
import com.mytutor.entities.Subject;
import com.mytutor.entities.TutorDetail;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.CertificateRepository;
import com.mytutor.repositories.EducationRepository;
import com.mytutor.repositories.RoleRepository;
import com.mytutor.repositories.SubjectRepository;
import com.mytutor.services.AccountService;
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
    private SubjectRepository subjectRepository;

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
     * @param accountId
     * @param accountDetailsDto
     * @return status code OK if updated successfully
     */
    @Override
    public ResponseEntity<?> updateAccountDetails(Integer accountId, AccountDetailsDto accountDetailsDto) {
        Account accountDB = getAccountById(accountId);

        if (accountDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found!");
        }

        modelMapper.map(accountDetailsDto, accountDB);

        accountRepository.save(accountDB);

        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully!");
    }



    private boolean checkRole(Account account) {
        Role role = roleRepository.findByRoleName("tutor").orElse(null);
        return !(role == null || !account.getRoles().contains(role));
    }

}
