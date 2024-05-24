/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.RoleName;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.entities.*;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.CertificateNotFoundException;
import com.mytutor.exceptions.EducationNotFoundException;
import com.mytutor.repositories.*;
import com.mytutor.services.TutorService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author Nguyen Van Dat
 */
@Service
public class TutorServiceImpl implements TutorService {

    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TutorDetailRepository tutorDetailRepository;

    @Override
    public ResponseEntity<List<ResponseAccountDetailsDto>> getAllTutors() {
        List<Account> tutors = accountRepository.findAllAccountsByRole(RoleName.TUTOR.name());  // Assuming RoleName enum exists
        List<ResponseAccountDetailsDto> tutorDtos = tutors.stream()
                .map(account -> ResponseAccountDetailsDto.mapToDto(account))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(tutorDtos);

    }

    @Override
    public ResponseEntity<List<EducationDto>> getListOfEducationsByTutorId(Integer tutorId) {

        List<Education> educations = educationRepository.findByAccountId(tutorId);
        List<EducationDto> educationDtos = educations.stream().map(e -> modelMapper.map(e, EducationDto.class)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(educationDtos);
    }

    @Override
    public ResponseEntity<List<CertificateDto>> getListOfCertificatesByTutorId(Integer tutorId) {

        List<Certificate> certificates = certificateRepository.findByAccountId(tutorId);
        List<CertificateDto> certificateDtos = certificates.stream().map(c -> modelMapper.map(c, CertificateDto.class)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(certificateDtos);
    }

    @Override
    public ResponseEntity<?> addAllEducations(Integer tutorId, List<EducationDto> educationDtos) {

        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        for (EducationDto educationDto : educationDtos) {

            Education education = modelMapper.map(educationDto, Education.class);
            education.setAccount(tutor);
            education.setVerified(false);

            educationRepository.save(education);
        }

        List<Education> educations = educationRepository.findByAccountId(tutorId);
        List<EducationDto> educationResponse = educations.stream().map(e -> modelMapper.map(e, EducationDto.class)).toList();

        return ResponseEntity.status(HttpStatus.OK).body(educationResponse);
    }

    @Override
    public ResponseEntity<?> addAllCertificates(Integer tutorId, List<CertificateDto> certificateDtos) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        for (CertificateDto certificateDto : certificateDtos) {
            Certificate certificate = modelMapper.map(certificateDto, Certificate.class);
            certificate.setAccount(tutor);
            certificate.setVerified(false);

            certificateRepository.save(certificate);
        }

        List<Certificate> certificates = certificateRepository.findByAccountId(tutorId);
        List<CertificateDto> certificateResponse = certificates.stream().map(c -> modelMapper.map(c, CertificateDto.class)).toList();

        return ResponseEntity.status(HttpStatus.OK).body(certificateResponse);

    }

    @Override
    public ResponseEntity<?> updateEducation(Integer tutorId, Integer educationId, EducationDto educationDto) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Education education = educationRepository.findById(Long.valueOf(educationId)).orElseThrow(() -> new EducationNotFoundException("Education not found"));

        if (education.getAccount().getId() != tutor.getId()) {
            throw new EducationNotFoundException("This education does not belong to this tutor");
        }

        education.setDegreeType(educationDto.getDegreeType());
        education.setUniversityName(educationDto.getUniversityName());
        education.setMajorName(educationDto.getMajorName());
        education.setSpecialization(educationDto.getSpecialization());
        education.setStartYear(educationDto.getStartYear());
        education.setEndYear(educationDto.getEndYear());
        education.setDiplomaUrl(educationDto.getDiplomaUrl());

        educationRepository.save(education);

        EducationDto educationResponse = modelMapper.map(education, EducationDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(educationResponse);
    }

    @Override
    public ResponseEntity<?> updateCertificate(Integer tutorId, Integer certificateId, CertificateDto certificateDto) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Certificate certificate = certificateRepository.findById(certificateId).orElseThrow(() -> new CertificateNotFoundException("Certificate not found"));

        if (certificate.getAccount().getId() != tutor.getId()) {
            throw new CertificateNotFoundException("This certificate does not belong to this tutor");
        }

        certificate.setCertificateName(certificateDto.getCertificateName());
        certificate.setCertificateUrl(certificateDto.getCertificateUrl());
        certificate.setDescription(certificateDto.getDescription());
        certificate.setIssuedBy(certificateDto.getIssuedBy());
        certificate.setIssuedYear(certificateDto.getIssuedYear());

        certificateRepository.save(certificate);

        CertificateDto certificateResponse = modelMapper.map(certificate, CertificateDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(certificateResponse);
    }

    @Override
    public ResponseEntity<?> deleteEducation(Integer tutorId, Integer educationId) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Education education = educationRepository.findById(Long.valueOf(educationId)).orElseThrow(() -> new EducationNotFoundException("Education not found"));

        if (education.getAccount().getId() != tutor.getId()) {
            throw new EducationNotFoundException("This education does not belong to this tutor");
        }

        educationRepository.delete(education);

        return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
    }

    @Override
    public ResponseEntity<?> deleteCertificate(Integer tutorId, Integer certificateId) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Certificate certificate = certificateRepository.findById(certificateId).orElseThrow(() -> new CertificateNotFoundException("Certificate not found"));

        if (certificate.getAccount().getId() != tutor.getId()) {
            throw new CertificateNotFoundException("This certificate does not belong to this tutor");
        }

        certificateRepository.delete(certificate);

        return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
    }

    @Override
    public ResponseEntity<?> addTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // neu accountid da nam trong danh sach thi return luon
        if (tutorDetailRepository.findByAccountId(accountId) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tutor description exists already!");
        }
        TutorDetail tutorDetail = modelMapper.map(tutorDescriptionDto, TutorDetail.class);

        tutorDetail.setAccount(account);
        tutorDetailRepository.save(tutorDetail);

        Set<Subject> subjects = new HashSet<>();
        for (Subject subject : tutorDescriptionDto.getSubjects()) {
            Subject existingSubject = subjectRepository.findBySubjectName(subject.getSubjectName())
                    .orElseGet(() -> subjectRepository.save(subject));
            subjects.add(existingSubject);
        }
        account.setSubjects(subjects);

        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body("Tutor description updated successfully!");
    }

}
