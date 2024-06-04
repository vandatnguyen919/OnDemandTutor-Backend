package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.RoleName;
import com.mytutor.constants.VerifyStatus;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.entities.*;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.repositories.*;
import com.mytutor.services.ModeratorService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 *
 * @author vothimaihoa
 */
@Service
public class ModeratorServiceImpl implements ModeratorService {

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    TutorDetailRepository tutorDetailRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ResponseEntity<?> checkAnEducation(int educationId, String status) {
        Education education = educationRepository.findById(educationId).get();
        education.setVerifyStatus(VerifyStatus.valueOf(status.toUpperCase()));
        educationRepository.save(education);
        EducationDto dto = modelMapper.map(education, EducationDto.class);
        return ResponseEntity.ok().body(dto);
    }

    @Override
    public ResponseEntity<?> checkACertificate(int certificateId, String status) {
        Certificate certificate = certificateRepository.findById(certificateId).get();
        certificate.setVerifyStatus(VerifyStatus.valueOf(status.toUpperCase()));
        certificateRepository.save(certificate);
        CertificateDto dto = modelMapper.map(certificate, CertificateDto.class);
        return ResponseEntity.ok().body(dto);
    }

    @Transactional
    @Override
    public ResponseEntity<?> checkTutor(Integer tutorId, String status) {
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        // nếu approved tutor -> set account: giữ role tutor, status thành ACTIVE + status của mọi bằng cấp chứng chỉ thành APPROVED
        // nếu reject tutor -> set account: role thành student và status ACTIVE + xóa tất cả bằng cấp chúng ch tutor details liên quan (cả trong firebase - FE xử lý)
        if (status.equalsIgnoreCase("approved")) {
            tutor.setStatus(AccountStatus.ACTIVE);
            educationRepository.updateEducationByTutorId(VerifyStatus.APPROVED, tutorId);
            certificateRepository.updateCertificateByTutorId(VerifyStatus.APPROVED, tutorId);
            return ResponseEntity.status(HttpStatus.OK).body("Approved tutor!");
        } else if (status.equalsIgnoreCase("rejected")) {
            Role roleStudent = roleRepository.findByRoleName(RoleName.STUDENT.toString()).get();
            tutor.setRoles(Collections.singleton(roleStudent));
            tutor.setStatus(AccountStatus.ACTIVE);
            tutorDetailRepository.deleteByAccountId(tutorId);
            educationRepository.deleteEducationByTutorId(tutorId);
            certificateRepository.deleteCertificateByTutorId(tutorId);
            return ResponseEntity.status(HttpStatus.OK).body("Rejected tutor!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status not allowed!");
    }

}
