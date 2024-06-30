/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.constants.FeedbackType;
import com.mytutor.dto.feedback.FeedbackDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.feedback.RequestFeedbackDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Feedback;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.FeedbackNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.repositories.FeedbackRepository;
import com.mytutor.services.FeedbackService;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public ResponseEntity<?> getAllReviews(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Feedback> feedbacks = feedbackRepository.findFeedbackByType(FeedbackType.REVIEW, pageable);
        List<Feedback> listOfFeedback = feedbacks.getContent();
        List<FeedbackDto> content = listOfFeedback.stream().map(FeedbackDto::mapToDto).toList();

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
        List<FeedbackDto> content = listOfFeedback.stream().map(FeedbackDto::mapToDto).toList();

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
    public ResponseEntity<?> createReview(Principal principal, int tutorId, RequestFeedbackDto requestFeedbackDto) {
        if (principal == null) {
            throw new BadCredentialsException("Token cannot be found or trusted");
        }

        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        Account student = accountRepository.findByEmail(principal.getName()).orElseThrow(() -> new AccountNotFoundException("Student not found"));

        if (!appointmentRepository.existsByTutorIdAndStudentIdAndStatus(tutor.getId(), student.getId(), AppointmentStatus.PAID)) {
            throw new BadCredentialsException("You have not registered this tutor yet");
        }

        // Map FeedbackDto to Feedback entity
        Feedback feedback = new Feedback();
        feedback.setRating(requestFeedbackDto.getRating());
        feedback.setContent(requestFeedbackDto.getContent());
        feedback.setCreatedAt(new Date()); // Set the current date as created date
        feedback.setModifiedAt(new Date()); // Set the current date as modified date
        feedback.setIsBanned(false); // Default to not banned
//        feedback.setType(feedbackDto.getType());
        feedback.setType(FeedbackType.REVIEW); // Default to review
        feedback.setCreatedBy(student);
        feedback.setTutor(tutor);

        // Save the feedback entity
        feedback = feedbackRepository.save(feedback);

        // Map the saved feedback entity back to DTO
        FeedbackDto savedFeedbackDto = FeedbackDto.mapToDto(feedback);

        // Return the response entity with the saved feedback DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFeedbackDto);
    }

    @Override
    public ResponseEntity<?> updateReviewById(int tutorId, int reviewId, RequestFeedbackDto requestFeedbackDto) {

        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        Feedback feedback = feedbackRepository.findById(reviewId).orElseThrow(() -> new FeedbackNotFoundException("Feedback not found"));

        if (tutor.getId() != feedback.getTutor().getId()) {
            throw new FeedbackNotFoundException("Feedback not belongs to this tutor");
        }

        // Update the fields of the feedback
        if (requestFeedbackDto.getRating() != null) {
            feedback.setRating(requestFeedbackDto.getRating());
        }
        if (requestFeedbackDto.getContent() != null) {
            feedback.setContent(requestFeedbackDto.getContent());
        }
        if (requestFeedbackDto.getIsBanned() != null) {
            feedback.setIsBanned(requestFeedbackDto.getIsBanned());
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
