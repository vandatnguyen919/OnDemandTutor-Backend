package com.mytutor.controllers;

import com.mytutor.dto.CheckEducationDto;
import com.mytutor.dto.tutor.EducationDto;
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
//    @PutMapping("/educations/{tutorId}")
//    public ResponseEntity<?> checkEducations(
//           @RequestBody CheckEducationDto checkEducationDto) {
//        return moderatorService.checkEducationOfATutor(educationDto, status);
//    }

    // duyet chung chi cua tutor - certificate
//    @PutMapping("/certificates/{tutorId}")
//    public ResponseEntity<?> checkCertificates(
//            @PathVariable Integer tutorId) {
//        return moderatorService.checkCertificatesOfTutor(tutorId);
//    }
//
//    // duyet tutor description
//    @PutMapping("/tutor-descriptions/{tutorId}")
//    public ResponseEntity<?> checkTutorDescriptions(
//            @PathVariable Integer tutorId) {
//        return moderatorService.checkTutorDescriptionsOfTutor(tutorId);
//    }
}
