package com.mytutor.controllers;

import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.salary.RequestWithdrawRequestDto;
import com.mytutor.dto.salary.ResponseWithdrawRequestDto;
import com.mytutor.dto.salary.UpdateWithdrawRequestDto;
import com.mytutor.entities.WithdrawRequest;
import com.mytutor.services.SalaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 *
 * @author vothimaihoa
 */
@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @GetMapping("/{tutorId}/salary")
    public ResponseEntity<Double> getTutorSalaryStatistic
            (@PathVariable Integer tutorId,
             @RequestParam Integer month,
             @RequestParam Integer year) {
        return ResponseEntity.status(HttpStatus.OK).body(salaryService.getMonthlySalaryOfATutor(tutorId,month,year));
    }

    @PostMapping("/{tutorId}")
    public ResponseEntity<ResponseWithdrawRequestDto> sendWithdrawRequest(
            Principal principal,
            @PathVariable Integer tutorId,
            @Valid @RequestBody RequestWithdrawRequestDto withdrawRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salaryService.createWithdrawRequest(principal, tutorId, withdrawRequest));
    }

    @GetMapping("/withdraw-requests")
    public ResponseEntity<PaginationDto<ResponseWithdrawRequestDto>> getWithdrawRequests(
            @RequestParam(required = false, defaultValue = "0") Integer pageNo,
            @RequestParam(required = false, defaultValue = "7") Integer pageSize
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(salaryService.getListOfWithdrawRequest(pageNo, pageSize));
    }

    @PutMapping("/withdraw-requests")
    public ResponseEntity<ResponseWithdrawRequestDto> updateWithdrawRequest(
            @RequestBody UpdateWithdrawRequestDto updateWithdrawRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(salaryService.updateWithdrawRequest(updateWithdrawRequestDto));
    }

    @PostMapping("/send-emails")
    public ResponseEntity<String> sendSalaryAnnouncementEmails(
            @RequestParam int month,
            @RequestParam int year
    ) {
        salaryService.sendSalaryAnnouncementEmail(month, year);
        return ResponseEntity.status(HttpStatus.OK).body("Email sent");
    }
}
