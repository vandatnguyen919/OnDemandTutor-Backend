package com.mytutor.services.impl;

import com.mytutor.dto.CheckEducationDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.entities.Education;
import com.mytutor.repositories.EducationRepository;
import com.mytutor.services.ModeratorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author vothimaihoa
 */
@Service
public class ModeratorServiceImpl implements ModeratorService {

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> checkEducationsOfTutor(CheckEducationDto educationDto, String status) {
        // lay ra education theo tutorId
        // neu status la approve -> update education.isVerified = true
        // tra ve education sau khi kiem tra
        Education education = educationRepository.findById(educationDto.getId()).get();
        education.setVerified(status.equalsIgnoreCase("Approved"));
        educationRepository.save(education);
        EducationDto dto = modelMapper.map(education, EducationDto.class);
        return ResponseEntity.ok().body(dto);
    }

//    @Override
//    public ResponseEntity<?> checkCertificatesOfTutor(Integer tutorId) {
//        return null;
//    }

//    @Override
//    public ResponseEntity<?> checkTutorDescriptionsOfTutor(Integer tutorId) {
//        return null;
//    }
}
