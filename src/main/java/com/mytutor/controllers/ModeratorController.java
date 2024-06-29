package com.mytutor.controllers;

import com.mytutor.dto.RequestCheckTutorDto;
import com.mytutor.services.ModeratorService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
