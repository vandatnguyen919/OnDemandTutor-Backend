/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.FeedbackType;
import com.mytutor.dto.FeedbackDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.tutor.TutorInfoDto;
import com.mytutor.entities.Feedback;
import com.mytutor.repositories.FeedbackRepository;
import com.mytutor.repositories.ReplyRepository;
import com.mytutor.services.FeedbackService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author Nguyen Van Dat
 */
@Service
public class FeedBackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Override
    public ResponseEntity<?> getAllReviews(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Feedback> feedbacks = feedbackRepository.findFeedbackByType(FeedbackType.REVIEW, pageable);
        List<Feedback> listOfFeedback = feedbacks.getContent();
        List<FeedbackDto> content = listOfFeedback.stream().map(f -> FeedbackDto.mapToDto(f)).toList();

        PaginationDto<FeedbackDto> feedbackResponseDto = new PaginationDto<>();
        feedbackResponseDto.setContent(content);
        feedbackResponseDto.setPageNo(feedbacks.getNumber());
        feedbackResponseDto.setPageSize(feedbacks.getSize());
        feedbackResponseDto.setTotalElements(feedbacks.getTotalElements());
        feedbackResponseDto.setTotalPages(feedbacks.getTotalPages());
        feedbackResponseDto.setLast(feedbacks.isLast());

        return ResponseEntity.status(HttpStatus.OK).body(feedbackResponseDto);
    }

    @Override
    public ResponseEntity<?> getReviewsByTutorId(int pageNo, int pageSize, int tutorId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ResponseEntity<?> getReviewById(int pageNo, int pageSize, int tutorId, int reviewId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ResponseEntity<?> createReview(int tutorId, FeedbackDto feedbackDto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ResponseEntity<?> updateReviewById(int tutorId, int reviewId, FeedbackDto feedbackDto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ResponseEntity<?> deleteReviewById(int tutorId, int reviewId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
