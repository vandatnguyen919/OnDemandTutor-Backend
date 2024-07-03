/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.Role;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.UpdateAccountDetailsDto;
import com.mytutor.dto.tutor.TutorInfoDto;
import com.mytutor.entities.Account;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.PhoneNumberAlreadyUsedException;
import com.mytutor.repositories.AccountRepository;

import com.mytutor.repositories.EducationRepository;
import com.mytutor.repositories.FeedbackRepository;
import com.mytutor.services.AccountService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private EducationRepository educationRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public AccountServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<?> getAccountsByRole(Integer pageNo, Integer pageSize, Role role) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Account> accountPage;
        if (role != null)
            accountPage = accountRepository.findByRoleOrderByCreatedAtDesc(role, pageable);
        else
            accountPage = accountRepository.findByOrderByCreatedAtDesc(pageable);

        List<Account> accounts = accountPage.getContent();

        if (role == Role.TUTOR) {
            List<TutorInfoDto> content = accounts.stream()
                    .map(a -> {
                        TutorInfoDto tutorInfoDto = TutorInfoDto.mapToDto(a, a.getTutorDetail());
                        tutorInfoDto.setAverageRating(feedbackRepository.getAverageRatingByAccount(a));
                        tutorInfoDto.setEducations(educationRepository.findByAccountId(a.getId(), true).stream()
                                .map(e -> modelMapper.map(e, TutorInfoDto.TutorEducation.class)).toList());
                        return tutorInfoDto;
                    })
                    .collect(Collectors.toList());

            PaginationDto<TutorInfoDto> tutorResponseDto = new PaginationDto<>();
            tutorResponseDto.setContent(content);
            tutorResponseDto.setPageNo(accountPage.getNumber());
            tutorResponseDto.setPageSize(accountPage.getSize());
            tutorResponseDto.setTotalElements(accountPage.getTotalElements());
            tutorResponseDto.setTotalPages(accountPage.getTotalPages());
            tutorResponseDto.setLast(accountPage.isLast());

            return ResponseEntity.status(HttpStatus.OK).body(tutorResponseDto);
        } else {
            List<ResponseAccountDetailsDto> accountDetailsDtos = accounts.stream().map(ResponseAccountDetailsDto::mapToDto).toList();

            PaginationDto<ResponseAccountDetailsDto> response = new PaginationDto<>();
            response.setContent(accountDetailsDtos);
            response.setPageNo(accountPage.getNumber());
            response.setPageSize(accountPage.getSize());
            response.setTotalElements(accountPage.getTotalElements());
            response.setTotalPages(accountPage.getTotalPages());
            response.setLast(accountPage.isLast());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @Override
    public ResponseEntity<?> banAccountById(Integer accountId) {

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        account.setStatus(AccountStatus.BANNED);
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body(account.getEmail() + " is banned");
    }

    @Override
    public Account getAccountById(Integer accountId) {

        return accountRepository.findById(accountId).orElse(null);
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