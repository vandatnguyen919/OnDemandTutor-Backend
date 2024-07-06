package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.QuestionStatus;
import com.mytutor.constants.Role;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.moderator.RequestCheckDocumentDto;
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
    public ResponseEntity<?> checkEducationsAndCertificatesByTutor(int tutorId, RequestCheckDocumentDto dto) {
        // Handle educations
        Account tutor = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
        if (!tutor.getRole().equals(Role.TUTOR)) {
            throw new InvalidStatusException("This account is not tutor");
        }

        handleApprovingEducations(tutor, dto.getApprovedEducations());

        handleApprovingCertificates(tutor, dto.getApprovedCertificates());

        return ResponseEntity.ok().body("Checked documents! Please send email to tutor!");
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

        return ResponseEntity.status(HttpStatus.OK).body("Checked tutor! Please send email to tutor!");
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
        handleApprovingEducations(tutor, dto.getApprovedEducations());

        // Handle certificates
        handleApprovingCertificates(tutor, dto.getApprovedCertificates());

        // Update tutor details
        TutorDetail tutorDetail = tutor.getTutorDetail();
        tutorDetail.setBackgroundDescription(dto.getBackgroundDescription());
        tutorDetail.setVideoIntroductionLink(dto.getVideoIntroductionLink());

        // Save tutor
        accountRepository.save(tutor);
    }

    private void handleApprovingEducations(Account tutor, List<Integer> approvedEducations) {
        List<Education> allEducations = educationRepository.findByAccountId(tutor.getId());
        List<Education> newEducationsToSave = new ArrayList<>();
        List<Education> educationsToDelete = new ArrayList<>();

        for (Education education : allEducations) {
            if (approvedEducations.contains(education.getId())) {
                education.setVerified(true);
                newEducationsToSave.add(education);
            } else if (!education.isVerified()) {
                educationsToDelete.add(education);
            }
        }
        educationRepository.saveAll(newEducationsToSave);
        educationRepository.deleteAll(educationsToDelete);
    }

    private void handleApprovingCertificates(Account tutor, List<Integer> approvedCertificates) {
        List<Certificate> allCertificates = certificateRepository.findByAccountId(tutor.getId());
        List<Certificate> newCertificatesToSave = new ArrayList<>();
        List<Certificate> certificatesToDelete = new ArrayList<>();

        for (Certificate certificate : allCertificates) {
            if (approvedCertificates.contains(certificate.getId())) {
                certificate.setVerified(true);
                newCertificatesToSave.add(certificate);
            } else if (!certificate.isVerified()){
                // ko dc duyet tu truoc khi goi api nay va hien tai cung khong duoc duyet -> xoa
                certificatesToDelete.add(certificate);
            }
        }
        certificateRepository.saveAll(newCertificatesToSave);
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
    public void sendApprovalEmail(String receiverEmail, String moderateMessage, boolean isApproved, String approvalType) {
        Account receiver = accountRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        String subject = getEmailSubjectAndContent(moderateMessage, receiver, isApproved, approvalType)[0];
        String content = getEmailSubjectAndContent(moderateMessage, receiver, isApproved, approvalType)[1];

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(receiverEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);
    }

    private String getStyle() {
        return "    <style>\n" +
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
                "    </style>\n";
    }

    private String[] getEmailSubjectAndContent(String moderateMessage, Account receiver, boolean isApproved, String approvalType) {
        String messageClass = isApproved ? "approvedMessage" : "rejectedMessage";
        String status = isApproved ? "approved" : "rejected";
        String subject = "";
        String title = "";
        String contentMessage = "";
        if ("question".equalsIgnoreCase(approvalType)) {
            title = "Question Review Status";
            subject = "[MyTutor] " + title;
            contentMessage = "We are inclined to inform you that your question has been reviewed and " +
                    "<span style=\"font-weight: bold;\">" + status + "</span> by our moderators. Here are detail messages from our moderators: ";
        } else if ("document".equalsIgnoreCase(approvalType)) {
            title = "Document Review Status";
            subject = "[MyTutor] " + title;
            contentMessage = "We are inclined to inform you that your qualifications/certificates has been reviewed and " +
                    "<span style=\"font-weight: bold;\">" + status + "</span> by our moderators. Here are detail messages from our moderators: ";
        } else {
            title = "Tutor Registration Status";
            subject = "[MyTutor] " + title;
            contentMessage = "We are inclined to inform you that your profile has been reviewed and " +
                    "<span style=\"font-weight: bold;\">" + status + "</span> by our moderators. Here are detail messages from our moderators: ";
        }

        String content = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>" + title + "</title>\n" +
                getStyle() +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>" + title + "</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear " + receiver.getFullName() + ",</p>\n" +
                "            <p>" + contentMessage + "</p>\n" +
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
        String[] result = new String[] {subject, content};
        return result;
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
                    tutorInfoDto.setSubjects(subjectRepository.findByTutorId(a.getId()).stream()
                            .map(s -> s.getSubjectName()).collect(Collectors.toSet()));
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


    @Override
    public PaginationDto<TutorInfoDto> getTutorListHasNotVerifiedDocuments(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Account> tutorListPage = accountRepository.findTutorByUnverifiedDocuments(Role.TUTOR, AccountStatus.ACTIVE, pageable);
        List<Account> listOfTutors = tutorListPage.getContent();

        List<TutorInfoDto> content = listOfTutors.stream()
                .map(a -> {
                    TutorInfoDto tutorInfoDto = TutorInfoDto.mapToDto(a, a.getTutorDetail());
                    tutorInfoDto.setSubjects(subjectRepository.findByTutorId(a.getId()).stream()
                            .map(s -> s.getSubjectName()).collect(Collectors.toSet()));
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
        return tutorResponseDto;
    }

}
