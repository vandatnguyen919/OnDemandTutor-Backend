/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.services;

import com.mytutor.dto.feedback.RequestFeedbackDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

/**
 *
 * @author Nguyen Van Dat
 */
public interface FeedbackService {

    ResponseEntity<?> getAllReviews(int pageNo, int pageSize);

    ResponseEntity<?> getReviewsByTutorId(int pageNo, int pageSize, int tutorId);

    ResponseEntity<?> getReviewById(int pageNo, int pageSize, int tutorId, int reviewId);

    ResponseEntity<?> getReviewsByTutorIdStudentId(int tutorId, int studentId);

    ResponseEntity<?> createReview(Principal principal, int tutorId, RequestFeedbackDto requestFeedbackDto);

    ResponseEntity<?> updateReviewById(int tutorId, int reviewId, RequestFeedbackDto requestFeedbackDto);

    ResponseEntity<?> deleteReviewById(int tutorId, int reviewId);
}
