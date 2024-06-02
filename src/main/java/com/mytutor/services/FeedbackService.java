/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.services;

import com.mytutor.dto.FeedbackDto;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Nguyen Van Dat
 */
public interface FeedbackService {

    public ResponseEntity<?> getAllReviews(int pageNo, int pageSize);

    public ResponseEntity<?> getReviewsByTutorId(int pageNo, int pageSize, int tutorId);

    public ResponseEntity<?> getReviewById(int pageNo, int pageSize, int tutorId, int reviewId);

    public ResponseEntity<?> createReview(int tutorId, FeedbackDto feedbackDto);

    public ResponseEntity<?> updateReviewById(int tutorId, int reviewId, FeedbackDto feedbackDto);

    public ResponseEntity<?> deleteReviewById(int tutorId, int reviewId);
}
