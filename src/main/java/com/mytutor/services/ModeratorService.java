package com.mytutor.services;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.QuestionStatus;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.moderator.RequestCheckTutorDto;
import com.mytutor.dto.student.QuestionDto;
import com.mytutor.dto.tutor.TutorInfoDto;
import com.mytutor.entities.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface ModeratorService {
    ResponseEntity<?> checkAnEducation(int educationId, String status); // sua lai: truyen vao dto gom list cac educationId trong requestbody

    ResponseEntity<?> checkACertificate(int certificateId, String status);

    ResponseEntity<?> checkTutor(Integer tutorId, String status, RequestCheckTutorDto dto);

    ResponseEntity<?> checkAQuestion(int questionId, String status);

    ResponseEntity<PaginationDto<TutorInfoDto>> getTutorListByStatus(AccountStatus status, int pageNo, int pageSize);

    ResponseEntity<PaginationDto<QuestionDto>> getQuestionListByStatus(QuestionStatus status, int pageNo, int pageSize);

    void sendApprovalEmail(String receiverEmail, String moderateMessage, boolean isApproved );
}
