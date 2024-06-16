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

    ResponseEntity<PaginationDto<TutorInfoDto>> getAllTutors(int pageNo,
                                                             int pageSize,
                                                             String subjects,
                                                             double priceMin,
                                                             double priceMax,
                                                             String tutorLevel,
                                                             String sortBy,
                                                             String keyword);

    ResponseEntity<TutorInfoDto> getTutorById(Integer id);

    ResponseEntity<List<EducationDto>> getListOfEducationsByTutorId(Integer tutorId, String isVerified);

    ResponseEntity<List<CertificateDto>> getListOfCertificatesByTutorId(Integer tutorId, String isVerified);

    ResponseEntity<?> addAllEducations(Integer tutorId, List<EducationDto> educationDtos);

    ResponseEntity<?> addAllCertificates(Integer tutorId, List<CertificateDto> certificateDtos);

    ResponseEntity<?> updateEducation(Integer tutorId, Integer educationId, EducationDto educationDto);

    ResponseEntity<?> updateCertificate(Integer tutorId, Integer certificateId, CertificateDto certificateDto);

    ResponseEntity<?> deleteEducation(Integer tutorId, Integer educationId);

    ResponseEntity<?> deleteCertificate(Integer tutorId, Integer certificateId);

    ResponseEntity<?> addTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto);

    ResponseEntity<?> updateTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto);

    ResponseEntity<?> getTutorDescriptionById(Integer accountId);
}
