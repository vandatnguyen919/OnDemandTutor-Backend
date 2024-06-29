package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.QuestionStatus;
import com.mytutor.constants.Role;
import com.mytutor.constants.VerifyStatus;
import com.mytutor.dto.CheckingDto;
import com.mytutor.dto.QuestionDto;
import com.mytutor.dto.RequestCheckTutorDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.entities.*;
import com.mytutor.exceptions.*;
import com.mytutor.repositories.*;
import com.mytutor.services.ModeratorService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Service
public class ModeratorServiceImpl implements ModeratorService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    TutorDetailRepository tutorDetailRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @Override
    public ResponseEntity<?> checkAnEducation(int educationId, String status) {
        Education education = educationRepository.findById(educationId).orElseThrow(
                () -> new EducationNotFoundException("Education not found!"));
        if (education.isVerified() || education.getVerifyStatus().equals(VerifyStatus.APPROVED)) {
            throw new EducationNotFoundException("Education has been checked!");
        }
        education.setVerifyStatus(VerifyStatus.valueOf(status.toUpperCase()));
        educationRepository.save(education);
        EducationDto dto = modelMapper.map(education, EducationDto.class);
        return ResponseEntity.ok().body(dto);
    }

    @Override
    public ResponseEntity<?> checkACertificate(int certificateId, String status) {
        Certificate certificate = certificateRepository.findById(certificateId).orElseThrow(
                () -> new EducationNotFoundException("Certificate not found!"));
        if (certificate.isVerified() || certificate.getVerifyStatus().equals(VerifyStatus.APPROVED)) {
            throw new EducationNotFoundException("Certificate has been checked!");
        }
        certificate.setVerifyStatus(VerifyStatus.valueOf(status.toUpperCase()));
        certificateRepository.save(certificate);
        CertificateDto dto = modelMapper.map(certificate, CertificateDto.class);
        return ResponseEntity.ok().body(dto);
    }

    @Transactional
    @Override
    public ResponseEntity<?> checkTutor(Integer tutorId, String status, RequestCheckTutorDto dto) {
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        // nếu approved tutor -> set account: giữ role tutor, status thành ACTIVE
        if (status.equalsIgnoreCase("approved")) {
            handleApprovingTutor(tutor, dto);
        } else if (status.equalsIgnoreCase("rejected")) {
            // nếu reject tutor -> set account: role thành student và status ACTIVE
            // + xóa tất cả bằng cấp chúng chỉ, tutor details liên quan (cả trong firebase - FE xử lý), timeslot
            handleRejectingTutor(tutor, dto);
        } else {
            throw new InvalidStatusException("Status not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Checked tutor!");
    }

    private void handleApprovingTutor(Account tutor, RequestCheckTutorDto dto) {
        tutor.setStatus(AccountStatus.ACTIVE);
        // handle subjects
        for (Subject s : tutor.getSubjects()) {
            // if subject not in approved list
            if (!dto.getApprovedSubjects().contains(s.getId())) {
                tutor.getSubjects().remove(s);
            }
        }

        // handle educations
        List<Education> allEducations = educationRepository.findByAccountId(tutor.getId());
        for (Education edu : allEducations) {
            if (dto.getApprovedEducations().contains(edu.getId())) {
                edu.setVerified(true);
                edu.setVerifyStatus(VerifyStatus.APPROVED);
            } else
                edu.setVerifyStatus(VerifyStatus.REJECTED);
            educationRepository.save(edu);
        }

        // handle certificates
        List<Certificate> allCertificates = certificateRepository.findByAccountId(tutor.getId());
        for (Certificate cert : allCertificates) {
            if (dto.getApprovedCertificates().contains(cert.getId())) {
                cert.setVerified(true);
                cert.setVerifyStatus(VerifyStatus.APPROVED);
            } else
                cert.setVerifyStatus(VerifyStatus.REJECTED);
            certificateRepository.save(cert);
        }
        tutor.getTutorDetail().setBackgroundDescription(dto.getBackgroundDescription());
        tutor.getTutorDetail().setVideoIntroductionLink(dto.getVideoIntroductionLink());
        accountRepository.save(tutor);
    }

    private void handleRejectingTutor(Account tutor, RequestCheckTutorDto dto) {
        tutor.setRole(Role.STUDENT);
        tutor.setStatus(AccountStatus.ACTIVE);
        TutorDetail tutorDetail = tutor.getTutorDetail();
        if (tutorDetail != null) {
            tutor.setTutorDetail(null); // Unlink the tutor detail before deleting
            tutorDetailRepository.delete(tutorDetail);
        }
        tutor.setSubjects(null);
        educationRepository.deleteEducationByTutorId(tutor.getId());
        certificateRepository.deleteCertificateByTutorId(tutor.getId());
        weeklyScheduleRepository.deleteScheduleByTutorId(tutor.getId());
        accountRepository.save(tutor);
    }

    // status: ok: UNSOLVED, ko ok: REJECTED
    @Override
    public ResponseEntity<?> checkAQuestion(int questionId, String status) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new QuestionNotFoundException("Question not found!"));
        if (!question.getStatus().equals(QuestionStatus.PROCESSING)) {
            throw new QuestionNotFoundException("Question has been checked!");
        }
        if (status.equalsIgnoreCase("unsolved") || status.equalsIgnoreCase("rejected")) {
            question.setStatus(QuestionStatus.valueOf(status.toUpperCase()));
            questionRepository.save(question);
        }
        QuestionDto dto = modelMapper.map(question, QuestionDto.class);
        return ResponseEntity.ok().body(dto);
    }

}
