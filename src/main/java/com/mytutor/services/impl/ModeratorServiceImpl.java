package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.QuestionStatus;
import com.mytutor.constants.Role;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.student.QuestionDto;
import com.mytutor.dto.moderator.RequestCheckTutorDto;
import com.mytutor.dto.tutor.CertificateDto;
import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.dto.tutor.TutorInfoDto;
import com.mytutor.entities.*;
import com.mytutor.exceptions.*;
import com.mytutor.repositories.*;
import com.mytutor.services.ModeratorService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public ResponseEntity<?> checkAnEducation(int educationId, String status) {
        Education education = educationRepository.findById(educationId).orElseThrow(
                () -> new EducationNotFoundException("Education not found!"));
        if (education.isVerified()) {
            throw new EducationNotFoundException("Education has been checked!");
        }
        educationRepository.save(education);
        EducationDto dto = modelMapper.map(education, EducationDto.class);
        return ResponseEntity.ok().body(dto);
    }

    @Override
    public ResponseEntity<?> checkACertificate(int certificateId, String status) {
        Certificate certificate = certificateRepository.findById(certificateId).orElseThrow(
                () -> new EducationNotFoundException("Certificate not found!"));
        if (certificate.isVerified()) {
            throw new EducationNotFoundException("Certificate has been checked!");
        }
        certificateRepository.save(certificate);
        CertificateDto dto = modelMapper.map(certificate, CertificateDto.class);
        return ResponseEntity.ok().body(dto);
    }

    @Transactional
    @Override
    public ResponseEntity<?> checkTutor(Integer tutorId, String status, RequestCheckTutorDto dto) {
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
        if (!tutor.getRole().equals(Role.TUTOR)) {
            throw new InvalidStatusException("This account is not tutor");
        }

        if (!tutor.getStatus().equals(AccountStatus.PROCESSING)) {
            throw new InvalidStatusException("This tutor has been moderated before!");
        }

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

        return ResponseEntity.status(HttpStatus.OK).body("Checked tutor! Please send email to tutor");
    }

    private void handleApprovingTutor(Account tutor, RequestCheckTutorDto dto) {
        // Set tutor status to ACTIVE
        tutor.setStatus(AccountStatus.ACTIVE);

        // Handle subjects
        Set<Subject> approvedSubjects = new HashSet<>();
        for (String subjectName : dto.getApprovedSubjects()) {
            approvedSubjects.add(subjectRepository.findBySubjectName(subjectName)
                    .orElseThrow(() -> new SubjectNotFoundException("Subject not found!")));
        }
        tutor.setSubjects(approvedSubjects);

        // Handle educations
        handleApprovingEducations(tutor, dto);

        // Handle certificates
        handleApprovingCertificates(tutor, dto);

        // Update tutor details
        TutorDetail tutorDetail = tutor.getTutorDetail();
        tutorDetail.setBackgroundDescription(dto.getBackgroundDescription());
        tutorDetail.setVideoIntroductionLink(dto.getVideoIntroductionLink());

        // Save tutor
        accountRepository.save(tutor);
    }

    private void handleApprovingEducations(Account tutor, RequestCheckTutorDto dto) {
        List<Education> allEducations = educationRepository.findByAccountId(tutor.getId());
        List<Education> educationsToSave = new ArrayList<>();
        List<Education> educationsToDelete = new ArrayList<>();

        for (Education education : allEducations) {
            if (dto.getApprovedEducations().contains(education.getId())) {
                education.setVerified(true);
                educationsToSave.add(education);
            } else {
                educationsToDelete.add(education);
            }
        }
        educationRepository.saveAll(educationsToSave);
        educationRepository.deleteAll(educationsToDelete);
    }

    private void handleApprovingCertificates(Account tutor, RequestCheckTutorDto dto) {
        List<Certificate> allCertificates = certificateRepository.findByAccountId(tutor.getId());
        List<Certificate> certificatesToSave = new ArrayList<>();
        List<Certificate> certificatesToDelete = new ArrayList<>();

        for (Certificate certificate : allCertificates) {
            if (dto.getApprovedCertificates().contains(certificate.getId())) {
                certificate.setVerified(true);
                certificatesToSave.add(certificate);
            } else {
                certificatesToDelete.add(certificate);
            }
        }
        certificateRepository.saveAll(certificatesToSave);
        certificateRepository.deleteAll(certificatesToDelete);
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

    @Override
    public void sendApprovalEmail(String receiverEmail, String moderateMessage, boolean isApproved ) {
        Account receiver = accountRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        String content = getEmailContent(moderateMessage, receiver, isApproved);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(receiverEmail);
            helper.setSubject("[MyTutor] Tutor Registration Status");
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);
    }

    private String getEmailContent(String moderateMessage, Account receiver, boolean isApproved) {
        String messageClass = isApproved ? "approvedMessage" : "rejectedMessage";
        String status = isApproved ? "approved" : "rejected";
        String content = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Tutor Registration Status</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f3f2f7;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            margin: 5px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1) !important; \n" +
                "        }\n" +
                "        .header {\n" +
                "            background: linear-gradient(90deg, #672DEF 0%, #FA6EAD 100%);\n" +
                "            color: #ffffff;\n" +
                "            padding: 10px 0;\n" +
                "            text-align: center;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .approvedMessage {\n" +
                "            background-color: #d4edda;\n" +
                "            color: #155724;\n" +
                "            padding: 10px;\n" +
                "            border-radius: 5px;\n" +
                "            display: inline-block;\n" +
                "            margin: 10px 0;\n" +
                "        }\n" +
                "        .rejectedMessage {\n" +
                "            background-color: #f8d7da;\n" +
                "            color: #721c24;\n" +
                "            padding: 10px;\n" +
                "            border-radius: 5px;\n" +
                "            display: inline-block;\n" +
                "            margin: 10px 0;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            margin-top: 20px;\n" +
                "            text-align: center;\n" +
                "            color: #777;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            margin-top: 10px;\n" +
                "            font-size: 16px;\n" +
                "            color: #ffffff !important;\n" +
                "            background: linear-gradient(90deg, #672DEF 0%, #FA6EAD 100%);\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Tutor Registration Status</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear " + receiver.getFullName() + ",</p>\n" +
                "            <p>We are inclined to inform you that your profile has been reviewed and " + "<span style=\"font-weight: bold;\">" + status + "</span> by our moderators. Here are detail messages from our moderators: </p>\n" +
                "            <p class=\"" + messageClass + "\">" + moderateMessage + "</p>\n" +
                "            <p>Thank you for your patience and welcome to our tutoring community!</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2024 MyTutor. All rights reserved.</p>\n" +
                "            <p><a href=\"http://localhost:5173\" class=\"button\">Visit Our Website</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
        return content;
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
        QuestionDto dto = QuestionDto.mapToDto(question, question.getSubject().getSubjectName());
        return ResponseEntity.ok().body(dto);
    }

    @Override
    public ResponseEntity<PaginationDto<TutorInfoDto>> getTutorListByStatus(AccountStatus status, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Account> tutorListPage = accountRepository.findByRoleAndStatus(Role.TUTOR, status, pageable);
        List<Account> listOfTutors = tutorListPage.getContent();

        List<TutorInfoDto> content = listOfTutors.stream()
                .map(a -> {
                    TutorInfoDto tutorInfoDto = TutorInfoDto.mapToDto(a, a.getTutorDetail());
                    tutorInfoDto.setEducations(educationRepository.findByAccountId(a.getId()).stream()
                            .map(e -> modelMapper.map(e, TutorInfoDto.TutorEducation.class)).toList());
                    return tutorInfoDto;
                })
                .collect(Collectors.toList());

        PaginationDto<TutorInfoDto> tutorResponseDto = new PaginationDto<>();
        tutorResponseDto.setContent(content);
        tutorResponseDto.setPageNo(tutorListPage.getNumber());
        tutorResponseDto.setPageSize(tutorListPage.getSize());
        tutorResponseDto.setTotalElements(tutorListPage.getTotalElements());
        tutorResponseDto.setTotalPages(tutorListPage.getTotalPages());
        tutorResponseDto.setLast(tutorListPage.isLast());
        return ResponseEntity.ok(tutorResponseDto);
    }

    @Override
    public ResponseEntity<PaginationDto<QuestionDto>> getQuestionListByStatus(QuestionStatus status, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Question> questionListPage = questionRepository.findByStatus(status, pageable);
        List<Question> listOfQuestions = questionListPage.getContent();

        List<QuestionDto> content = listOfQuestions.stream()
                .map(q -> {
                    QuestionDto questionDto = QuestionDto.mapToDto(q, q.getSubject().getSubjectName());
                    return questionDto;
                })
                .collect(Collectors.toList());

        PaginationDto<QuestionDto> questionResponseDto = new PaginationDto<>();
        questionResponseDto.setContent(content);
        questionResponseDto.setPageNo(questionListPage.getNumber());
        questionResponseDto.setPageSize(questionListPage.getSize());
        questionResponseDto.setTotalElements(questionListPage.getTotalElements());
        questionResponseDto.setTotalPages(questionListPage.getTotalPages());
        questionResponseDto.setLast(questionListPage.isLast());
        return ResponseEntity.ok(questionResponseDto);
    }

}
