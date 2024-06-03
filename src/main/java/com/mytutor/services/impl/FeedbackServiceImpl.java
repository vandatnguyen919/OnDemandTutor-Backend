/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.FeedbackType;
import com.mytutor.dto.FeedbackDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Feedback;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.FeedbackNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.FeedbackRepository;
import com.mytutor.services.FeedbackService;
import java.util.Date;
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
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

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

        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Feedback> feedbacks = feedbackRepository.findFeedbackByTypeAndTutorId(FeedbackType.REVIEW, tutor, pageable);
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
    public ResponseEntity<?> getReviewById(int pageNo, int pageSize, int tutorId, int reviewId) {

        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        Feedback feedback = feedbackRepository.findById(reviewId).orElseThrow(() -> new FeedbackNotFoundException("Feedback not found"));

        if (tutor.getId() != feedback.getTutor().getId()) {
            throw new FeedbackNotFoundException("Feedback not belongs to this tutor");
        }

        FeedbackDto feedbackDto = FeedbackDto.mapToDto(feedback);

        return ResponseEntity.status(HttpStatus.OK).body(feedbackDto);
    }

    @Override
    public ResponseEntity<?> createReview(int tutorId, FeedbackDto feedbackDto) {

        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        Account creator = accountRepository.findById(feedbackDto.getCreatedBy()).orElseThrow(() -> new AccountNotFoundException("Creator not found"));

        // Map FeedbackDto to Feedback entity
        Feedback feedback = new Feedback();
        feedback.setRating(feedbackDto.getRating());
        feedback.setContent(feedbackDto.getContent());
        feedback.setCreatedAt(new Date()); // Set the current date as created date
        feedback.setModifiedAt(new Date()); // Set the current date as modified date
        feedback.setIsBanned(false); // Default to not banned
//        feedback.setType(feedbackDto.getType());
        feedback.setType(FeedbackType.REVIEW); // Default to review
        feedback.setCreatedBy(creator);
        feedback.setTutor(tutor);

        // Save the feedback entity
        feedback = feedbackRepository.save(feedback);

        // Map the saved feedback entity back to DTO
        FeedbackDto savedFeedbackDto = FeedbackDto.mapToDto(feedback);

        // Return the response entity with the saved feedback DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFeedbackDto);
    }

    @Override
    public ResponseEntity<?> updateReviewById(int tutorId, int reviewId, FeedbackDto feedbackDto) {

        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        Feedback feedback = feedbackRepository.findById(reviewId).orElseThrow(() -> new FeedbackNotFoundException("Feedback not found"));

        if (tutor.getId() != feedback.getTutor().getId()) {
            throw new FeedbackNotFoundException("Feedback not belongs to this tutor");
        }

        // Update the fields of the feedback
        if (feedbackDto.getRating() != null) {
            feedback.setRating(feedbackDto.getRating());
        }
        if (feedbackDto.getContent() != null) {
            feedback.setContent(feedbackDto.getContent());
        }
        if (feedbackDto.getIsBanned() != null) {
            feedback.setIsBanned(feedbackDto.getIsBanned());
        }
        feedback.setModifiedAt(new Date()); // Set the current date as modified date

        // Save the updated feedback entity
        feedback = feedbackRepository.save(feedback);

        // Map the updated feedback entity back to DTO
        FeedbackDto updatedFeedbackDto = FeedbackDto.mapToDto(feedback);

        // Return the response entity with the updated feedback DTO
        return ResponseEntity.status(HttpStatus.OK).body(updatedFeedbackDto);
    }

    @Override
    public ResponseEntity<?> deleteReviewById(int tutorId, int reviewId) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        Feedback feedback = feedbackRepository.findById(reviewId).orElseThrow(() -> new FeedbackNotFoundException("Feedback not found"));

        if (tutor.getId() != feedback.getTutor().getId()) {
            throw new FeedbackNotFoundException("Feedback not belongs to this tutor");
        }

        // Delete the feedback
        feedbackRepository.delete(feedback);

        return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
    }

}
