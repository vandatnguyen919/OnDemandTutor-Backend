package com.mytutor.controllers;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.QuestionStatus;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.moderator.RequestCheckTutorDto;
import com.mytutor.dto.moderator.TutorVerificationEmailDto;
import com.mytutor.dto.student.QuestionDto;
import com.mytutor.dto.tutor.TutorInfoDto;
import com.mytutor.services.ModeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author vothimaihoa
 */
@RestController
@RequestMapping("/api/moderators")
public class ModeratorController {
    @Autowired
    ModeratorService moderatorService;

    // duyet bang cap cua tutor - education
    @PutMapping("/educations/{educationId}")
    public ResponseEntity<?> checkEducations(
           @PathVariable int educationId,
           @RequestParam String status) {
        return moderatorService.checkAnEducation(educationId, status);
    }

    // duyet chung chi cua tutor - certificate
    @PutMapping("/certificates/{certificateId}")
    public ResponseEntity<?> checkCertificates(
            @PathVariable int certificateId,
            @RequestParam String status) {
        return moderatorService.checkACertificate(certificateId, status);
    }

    // duyet cau hoi
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<?> checkQuestions(
            @PathVariable int questionId,
            @RequestParam String status) {
        return moderatorService.checkAQuestion(questionId, status);
    }

    // duyet tutor description
    @PutMapping("/tutors/{tutorId}")
    public ResponseEntity<?> checkTutor(
            @PathVariable Integer tutorId,
            @RequestParam String status,
            @RequestBody RequestCheckTutorDto dto) {
        return moderatorService.checkTutor(tutorId, status, dto);
    }

    @GetMapping("/tutors")
    public ResponseEntity<PaginationDto<TutorInfoDto>> getTutorListByStatus(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "status") AccountStatus status) {
        return moderatorService.getTutorListByStatus(status, pageNo, pageSize);
    }

    @GetMapping("/questions")
    public ResponseEntity<PaginationDto<QuestionDto>> getQuestionListByStatus(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "status") QuestionStatus status) {
        return moderatorService.getQuestionListByStatus(status, pageNo, pageSize);
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<String> sendVerificationEmail(
            @RequestParam(value = "approvalType", defaultValue = "", required = false) String approvalType,
            @RequestBody TutorVerificationEmailDto dto
    ) {
        moderatorService.sendApprovalEmail(dto.getEmail(), dto.getModeratorMessage(), dto.isApproved(), approvalType);
        return ResponseEntity.status(HttpStatus.OK).body("Verification email sent");
    }
}
