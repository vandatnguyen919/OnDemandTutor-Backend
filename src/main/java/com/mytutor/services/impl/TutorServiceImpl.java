/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Certificate;
import com.mytutor.entities.Education;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.CertificateNotFoundException;
import com.mytutor.exceptions.EducationNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.CertificateRepository;
import com.mytutor.repositories.EducationRepository;
import com.mytutor.services.TutorService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Nguyen Van Dat
 */
public class TutorServiceImpl implements TutorService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<List<EducationDto>> getListOfEducationsByTutorId(Integer tutorId) {

        List<Education> educations = educationRepository.findByTutorId(tutorId);
        List<EducationDto> educationDtos = educations.stream().map(e -> modelMapper.map(e, EducationDto.class)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(educationDtos);
    }

    @Override
    public ResponseEntity<List<CertificateDto>> getListOfCertificatesByTutorId(Integer tutorId) {

        List<Certificate> certificates = certificateRepository.findByTutorId(tutorId);
        List<CertificateDto> certificateDtos = certificates.stream().map(c -> modelMapper.map(c, CertificateDto.class)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(certificateDtos);
    }

    @Override
    public ResponseEntity<?> addEducations(Integer tutorId, List<EducationDto> educationDtos) {

        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        for (EducationDto educationDto : educationDtos) {

            Education education = modelMapper.map(educationDto, Education.class);
            education.setAccount(tutor);
            education.setVerified(false);

            educationRepository.save(education);
        }

        List<Education> educations = educationRepository.findByTutorId(tutorId);
        List<EducationDto> educationResponse = educations.stream().map(e -> modelMapper.map(e, EducationDto.class)).toList();

        return ResponseEntity.status(HttpStatus.OK).body(educationResponse);
    }

    @Override
    public ResponseEntity<?> addCertificates(Integer tutorId, List<CertificateDto> certificateDtos) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        for (CertificateDto certificateDto : certificateDtos) {
            Certificate certificate = modelMapper.map(certificateDto, Certificate.class);
            certificate.setAccount(tutor);
            certificate.setVerified(false);

            certificateRepository.save(certificate);
        }

        List<Certificate> certificates = certificateRepository.findByTutorId(tutorId);
        List<CertificateDto> certificateResponse = certificates.stream().map(c -> modelMapper.map(c, CertificateDto.class)).toList();

        return ResponseEntity.status(HttpStatus.OK).body(certificateResponse);
    }

    @Override
    public ResponseEntity<?> updateEducation(Integer tutorId, Integer educationId, EducationDto educationDto) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Education education = educationRepository.findById(educationId).orElseThrow(() -> new EducationNotFoundException("Education not found"));

        if (education.getAccount().getId() != tutor.getId()) {
            throw new EducationNotFoundException("This education does not belong to this tutor");
        }

//        if (!checkRole(account)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account is not a tutor");
//        }
        education = modelMapper.map(educationDto, Education.class);
        education.setAccount(tutor);
        education.setVerified(false);

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

//        if (!checkRole(account)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account is not a tutor");
//        }
        certificate = modelMapper.map(certificateDto, Certificate.class);
        certificate.setAccount(tutor);
        certificate.setVerified(false);

        certificateRepository.save(certificate);

        CertificateDto certificateResponse = modelMapper.map(certificate, CertificateDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(certificateResponse);
    }

    @Override
    public ResponseEntity<?> deleteEducation(Integer tutorId, Integer educationId) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Education education = educationRepository.findById(educationId).orElseThrow(() -> new EducationNotFoundException("Education not found"));

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

}
