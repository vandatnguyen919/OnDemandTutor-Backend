/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.services;

import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.tutor.*;

import java.util.List;

import org.springframework.http.ResponseEntity;

/**
 *
 * @author Nguyen Van Dat
 */
public interface TutorService {

    public ResponseEntity<PaginationDto<TutorInfoDto>> getAllTutors(int pageNo, int pageSize);

    public ResponseEntity<TutorInfoDto> getTutorById(Integer id);

    public ResponseEntity<List<EducationDto>> getListOfEducationsByTutorId(Integer tutorId);

    public ResponseEntity<List<CertificateDto>> getListOfCertificatesByTutorId(Integer tutorId);

    public ResponseEntity<?> addAllEducations(Integer tutorId, List<EducationDto> educationDtos);

    public ResponseEntity<?> addAllCertificates(Integer tutorId, List<CertificateDto> certificateDtos);

    public ResponseEntity<?> updateEducation(Integer tutorId, Integer educationId, EducationDto educationDto);

    public ResponseEntity<?> updateCertificate(Integer tutorId, Integer certificateId, CertificateDto certificateDto);

    public ResponseEntity<?> deleteEducation(Integer tutorId, Integer educationId);

    public ResponseEntity<?> deleteCertificate(Integer tutorId, Integer certificateId);

    public ResponseEntity<?> addTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto);

    public ResponseEntity<?> updateTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto);

    public ResponseEntity<?> getTutorDescriptionById(Integer accountId);
}
