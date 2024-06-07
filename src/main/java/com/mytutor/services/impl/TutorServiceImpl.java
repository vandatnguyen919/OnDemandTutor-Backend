/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.DegreeType;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorDescriptionDto;
import com.mytutor.dto.tutor.TutorInfoDto;
import com.mytutor.entities.*;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.CertificateNotFoundException;
import com.mytutor.exceptions.EducationNotFoundException;
import com.mytutor.exceptions.SubjectNotFoundException;
import com.mytutor.repositories.*;
import com.mytutor.services.TutorService;

import java.util.*;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Nguyen Van Dat
 */
@Service
public class TutorServiceImpl implements TutorService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountRepositoryCustom accountRepositoryCustom;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TutorDetailRepository tutorDetailRepository;

    @Override
    public ResponseEntity<PaginationDto<TutorInfoDto>> getAllTutors(int pageNo,
                                                                    int pageSize,
                                                                    String subjects,
                                                                    double priceMin,
                                                                    double priceMax,
                                                                    String tutorLevel,
                                                                    String sortBy,
                                                                    String keyword) {

        // Parse string (Eg: "maths,physics,chemistry") into set of subject string name
        Set<String> subjectSet = subjects.equalsIgnoreCase("all") ? null
                : Arrays.stream(subjects.split("[,\\s+]+")).map(s -> s.trim().toLowerCase()).collect(Collectors.toSet());

        // Parse string (Eg: "associate,bachelor,master,doctoral") into set of Degree Type
        Set<DegreeType> tutorLevelSet = tutorLevel.equalsIgnoreCase("all") ? null
                : Arrays.stream(tutorLevel.split("[,\\s+]+")).map(DegreeType::getDegreeType).collect(Collectors.toSet());

        // Get active tutors only
        List<AccountStatus> listOfStatus = List.of(AccountStatus.ACTIVE);

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Account> tutors = accountRepositoryCustom.findTutorsByFilters(subjectSet, priceMin, priceMax, tutorLevelSet, sortBy, keyword, listOfStatus, pageable);
        List<Account> listOfTutors = tutors.getContent();

        List<TutorInfoDto> content = listOfTutors.stream()
                .map(a -> {
                    TutorDetail td = tutorDetailRepository.findByAccountId(a.getId())
                            .orElse(new TutorDetail());
                    TutorInfoDto tutorInfoDto = TutorInfoDto.mapToDto(a, td);
                    tutorInfoDto.setAverageRating(feedbackRepository.getAverageRatingByAccount(a));
                    tutorInfoDto.setEducations(educationRepository.findByAccountId(a.getId()).stream()
                            .map(e -> modelMapper.map(e, TutorInfoDto.TutorEducation.class)).toList());
                    return tutorInfoDto;
                })
                .collect(Collectors.toList());

        PaginationDto<TutorInfoDto> tutorResponseDto = new PaginationDto<>();
        tutorResponseDto.setContent(content);
        tutorResponseDto.setPageNo(tutors.getNumber());
        tutorResponseDto.setPageSize(tutors.getSize());
        tutorResponseDto.setTotalElements(tutors.getTotalElements());
        tutorResponseDto.setTotalPages(tutors.getTotalPages());
        tutorResponseDto.setLast(tutors.isLast());

        return ResponseEntity.status(HttpStatus.OK).body(tutorResponseDto);
    }

    @Override
    public ResponseEntity<TutorInfoDto> getTutorById(Integer tutorId) {
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        TutorDetail td = tutorDetailRepository.findByAccountId(tutor.getId())
                .orElse(new TutorDetail());
        TutorInfoDto tutorInfoDto = TutorInfoDto.mapToDto(tutor, td);
        tutorInfoDto.setAverageRating(feedbackRepository.getAverageRatingByAccount(tutor));
        tutorInfoDto.setEducations(educationRepository.findByAccountId(tutor.getId()).stream()
                .map(e -> modelMapper.map(e, TutorInfoDto.TutorEducation.class)).toList());

        return ResponseEntity.status(HttpStatus.OK).body(tutorInfoDto);
    }

    @Override
    public ResponseEntity<List<EducationDto>> getListOfEducationsByTutorId(Integer tutorId) {

        List<Education> educations = educationRepository.findByAccountId(tutorId);
        List<EducationDto> educationDtos = educations.stream()
                .map(e -> modelMapper.map(e, EducationDto.class)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(educationDtos);
    }

    @Override
    public ResponseEntity<List<CertificateDto>> getListOfCertificatesByTutorId(Integer tutorId) {

        List<Certificate> certificates = certificateRepository.findByAccountId(tutorId);
        List<CertificateDto> certificateDtos = certificates.stream()
                .map(c -> modelMapper.map(c, CertificateDto.class)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(certificateDtos);
    }

    @Override
    public ResponseEntity<?> addAllEducations(Integer tutorId, List<EducationDto> educationDtos) {

        Account tutor = accountRepository.findById(tutorId).orElseThrow(
                () -> new AccountNotFoundException("Account not found"));

        for (EducationDto educationDto : educationDtos) {

            Education education = modelMapper.map(educationDto, Education.class);
            education.setAccount(tutor);
            education.setVerified(false);
            System.out.println(educationDto.getDegreeType().toString());
            education.setDegreeType(DegreeType.valueOf(educationDto.getDegreeType().toUpperCase()));

            educationRepository.save(education);
        }

        List<Education> educations = educationRepository.findByAccountId(tutorId);
        List<EducationDto> educationResponse = educations.stream()
                .map(e -> modelMapper.map(e, EducationDto.class)).toList();

        return ResponseEntity.status(HttpStatus.OK).body(educationResponse);
    }

    @Override
    public ResponseEntity<?> addAllCertificates(Integer tutorId, List<CertificateDto> certificateDtos) {
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        for (CertificateDto certificateDto : certificateDtos) {
            Certificate certificate = modelMapper.map(certificateDto, Certificate.class);
            certificate.setAccount(tutor);
            certificate.setVerified(false);

            certificateRepository.save(certificate);
        }

        List<Certificate> certificates = certificateRepository.findByAccountId(tutorId);
        List<CertificateDto> certificateResponse = certificates.stream().map(c -> modelMapper
                .map(c, CertificateDto.class)).toList();

        return ResponseEntity.status(HttpStatus.OK).body(certificateResponse);

    }

    @Override
    public ResponseEntity<?> updateEducation(Integer tutorId, Integer educationId, EducationDto educationDto) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(
                () -> new AccountNotFoundException("Account not found"));

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EducationNotFoundException("Education not found"));

        if (education.getAccount().getId() != tutor.getId()) {
            throw new EducationNotFoundException("This education does not belong to this tutor");
        }

        education.setDegreeType(DegreeType.valueOf(educationDto.getDegreeType().toUpperCase()));
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
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new CertificateNotFoundException("Certificate not found"));

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
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EducationNotFoundException("Education not found"));

        if (education.getAccount().getId() != tutor.getId()) {
            throw new EducationNotFoundException("This education does not belong to this tutor");
        }

        educationRepository.delete(education);

        return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
    }

    @Override
    public ResponseEntity<?> deleteCertificate(Integer tutorId, Integer certificateId) {
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new CertificateNotFoundException("Certificate not found"));

        if (certificate.getAccount().getId() != tutor.getId()) {
            throw new CertificateNotFoundException("This certificate does not belong to this tutor");
        }

        certificateRepository.delete(certificate);

        return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
    }

    @Override
    public ResponseEntity<?> addTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        // neu accountid da nam trong danh sach thi return luon
        if (tutorDetailRepository.findByAccountId(accountId).orElse(null) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tutor description exists already!");
        }
        TutorDetail tutorDetail = modelMapper.map(tutorDescriptionDto, TutorDetail.class);

        tutorDetail.setAccount(account);
        tutorDetailRepository.save(tutorDetail);

        Set<Subject> subjects = new HashSet<>();
        for (String subjectName : tutorDescriptionDto.getSubjects()) {
            Subject subject = subjectRepository.findBySubjectName(subjectName)
                    .orElseThrow(() -> new SubjectNotFoundException("Subject not found!"));
            subjects.add(subject);
        }
        account.setSubjects(subjects);

        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body("Tutor description added successfully!");
    }

    @Override
    public ResponseEntity<?> updateTutorDescription(Integer accountId, TutorDescriptionDto tutorDescriptionDto) {
        TutorDetail tutorDetail = tutorDetailRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No tutor detail found!"));
        ;

        String background = tutorDescriptionDto.getBackgroundDescription();
        if (background != null) {
            tutorDetail.setBackgroundDescription(tutorDescriptionDto.getBackgroundDescription());
        }

        String meetingLink = tutorDescriptionDto.getMeetingLink();
        if (meetingLink != null) {
            tutorDetail.setMeetingLink(meetingLink);
        }

        Double price = tutorDescriptionDto.getTeachingPricePerHour();
        if (price != null) {
            tutorDetail.setTeachingPricePerHour(price);
        }

        String video = tutorDescriptionDto.getVideoIntroductionLink();
        if (video != null) {
            tutorDetail.setVideoIntroductionLink(video);
        }

        Set<Subject> subjects = new HashSet<>();
        if (!tutorDescriptionDto.getSubjects().isEmpty()) {
            for (String subjectName : tutorDescriptionDto.getSubjects()) {
                Subject subject = subjectRepository.findBySubjectName(subjectName)
                        .orElseThrow(() -> new SubjectNotFoundException("Subject not found!"));
                subjects.add(subject);
            }
        }
        tutorDetail.getAccount().setSubjects(subjects);

        tutorDetailRepository.save(tutorDetail);

        return ResponseEntity.status(HttpStatus.OK).body("Tutor description updated successfully!");
    }

    @Override
    public ResponseEntity<?> getTutorDescriptionById(Integer accountId) {
        TutorDetail tutorDetail = tutorDetailRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No tutor detail found!"));
        TutorDescriptionDto tutorDescriptionDto = modelMapper.map(tutorDetail, TutorDescriptionDto.class);
        Set<String> subjectNames = new HashSet<>();
        for (Subject s : tutorDetail.getAccount().getSubjects()) {
            subjectNames.add(s.getSubjectName());
        }
        tutorDescriptionDto.setSubjects(subjectNames);
        return ResponseEntity.status(HttpStatus.OK).body(tutorDescriptionDto);
    }

}
