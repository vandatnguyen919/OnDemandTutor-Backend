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
    ResponseEntity<?> checkEducationsOfTutor(CheckEducationDto educationDto, String status);
//    ResponseEntity<?> checkCertificatesOfTutor(Integer tutorId);
//    ResponseEntity<?> checkTutorDescriptionsOfTutor(Integer tutorId);

}
