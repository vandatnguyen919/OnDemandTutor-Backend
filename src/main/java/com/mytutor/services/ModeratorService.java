package com.mytutor.services;

import com.mytutor.dto.CheckEducationDto;
import com.mytutor.dto.tutor.EducationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface ModeratorService {
    ResponseEntity<?> checkAnEducation(int educationId, String status); // sua lai: truyen vao dto gom list cac educationId trong requestbody
    ResponseEntity<?> checkACertificate(int certificateId, String status);
    ResponseEntity<?> checkTutor(Integer tutorId, String status);

}
