/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.services.TutorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Nguyen Van Dat
 */
@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    @Autowired
    TutorService tutorService;

//    // add pagination later
//    @GetMapping("/")
//    public ResponseEntity<List<ResponseAccountDetailsDto>> getAllTutors() {
//        return tutorService.getAllTutors();
//    }
//
//    @GetMapping("/{tutorId}/educations")
//    public ResponseEntity<List<EducationDto>> getListOfEducationsByTutorId(
//            @PathVariable Integer tutorId) {
//        return tutorService.getListOfEducationsByTutorId(tutorId);
//    }
//
//    @GetMapping("/{tutorId}/certificates")
//    public ResponseEntity<List<CertificateDto>> getListOfCertificatesByTutorId(
//            @PathVariable Integer tutorId) {
//        return tutorService.getListOfCertificatesByTutorId(tutorId);
//    }

    @PostMapping("/{tutorId}/educations")
    public ResponseEntity<?> addAllEducations(
            @PathVariable Integer tutorId,
            @RequestBody List<EducationDto> educationDtos) {
        return tutorService.addAllEducations(tutorId, educationDtos);
    }

    @PostMapping("/{tutorId}/certificates")
    public ResponseEntity<?> addAllCertificates(
            @PathVariable Integer tutorId,
            @RequestBody List<CertificateDto> certificateDtos) {
        return tutorService.addAllCertificates(tutorId, certificateDtos);
    }

    @PutMapping("/{tutorId}/educations/{educationId}")
    public ResponseEntity<?> updateEducation(
            @PathVariable Integer tutorId,
            @PathVariable Integer educationId,
            @RequestBody EducationDto educationDto) {
        return tutorService.updateEducation(tutorId, educationId, educationDto);
    }

    @PutMapping("/{tutorId}/certificates/{certificateId}")
    public ResponseEntity<?> updateCertificate(
            @PathVariable Integer tutorId,
            @PathVariable Integer certificateId,
            @RequestBody CertificateDto certificateDto) {
        return tutorService.updateCertificate(tutorId, certificateId, certificateDto);
    }

    @DeleteMapping("/{tutorId}/educations/{educationId}")
    public ResponseEntity<?> deleteEducation(
            @PathVariable Integer tutorId,
            @PathVariable Integer educationId) {
        return tutorService.deleteEducation(tutorId, educationId);
    }

    @DeleteMapping("/{tutorId}/certificates/{certificateId}")
    public ResponseEntity<?> deleteCertificate(
            @PathVariable Integer tutorId,
            @PathVariable Integer certificateId) {
        return tutorService.deleteEducation(tutorId, certificateId);
    }

    @PostMapping("/{accountId}/tutor-description")
    public ResponseEntity<?> editTutorDescription (
            @PathVariable Integer accountId,
            @RequestBody TutorDescriptionDto tutorDescriptionDto){
        return tutorService.updateTutorDescription(accountId, tutorDescriptionDto);
    }
}
