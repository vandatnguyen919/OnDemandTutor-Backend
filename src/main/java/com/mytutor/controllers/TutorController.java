package com.mytutor.controllers;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.tutor.*;
import com.mytutor.services.TutorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Nguyen Van Dat
 */
@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    @Autowired
    TutorService tutorService;

    @GetMapping("")
    public ResponseEntity<PaginationDto<TutorInfoDto>> getAllTutors(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "specialty", defaultValue = "all", required = false) String subjects,
            @RequestParam(value = "priceMin", defaultValue = "0", required = false) double priceMin,
            @RequestParam(value = "priceMax", defaultValue = "9999999999", required = false) double priceMax,
            @RequestParam(value = "tutorLevel", defaultValue = "all", required = false) String tutorLevel,
            @RequestParam(value = "sortBy", defaultValue = "price", required = false) String sortBy,
            @RequestParam(value = "searchKeyword", defaultValue = "", required = false) String keyword) {
        return tutorService.getAllTutors(pageNo, pageSize, subjects, priceMin, priceMax, tutorLevel, sortBy, keyword);
    }

    @GetMapping("/{tutorId}")
    public ResponseEntity<TutorInfoDto> getTutorById(
            @PathVariable Integer tutorId ) {
        return tutorService.getTutorById(tutorId);
    }

    @GetMapping("/{tutorId}/educations")
    public ResponseEntity<List<EducationDto>> getListOfEducationsByTutorId(
            @PathVariable Integer tutorId) {
        return tutorService.getListOfEducationsByTutorId(tutorId);
    }

    @GetMapping("/{tutorId}/certificates")
    public ResponseEntity<List<CertificateDto>> getListOfCertificatesByTutorId(
            @PathVariable Integer tutorId) {
        return tutorService.getListOfCertificatesByTutorId(tutorId);
    }

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

    // insert tutor-description
    @PostMapping("/{tutorId}/tutor-description")
    public ResponseEntity<?> addTutorDescription(
            @PathVariable Integer tutorId,
            @RequestBody TutorDescriptionDto tutorDescriptionDto) {
        return tutorService.addTutorDescription(tutorId, tutorDescriptionDto);
    }

    // update tutor-description
    @PutMapping("/{tutorId}/tutor-description")
    public ResponseEntity<?> updateTutorDescription(
            @PathVariable Integer tutorId,
            @RequestBody TutorDescriptionDto tutorDescriptionDto) {
        return tutorService.updateTutorDescription(tutorId, tutorDescriptionDto);
    }

    // get tutor-description
    @GetMapping("/{tutorId}/tutor-description")
    public ResponseEntity<?> getTutorDescriptionById(
            @PathVariable Integer tutorId) {
        return tutorService.getTutorDescriptionById(tutorId);
    }

}
