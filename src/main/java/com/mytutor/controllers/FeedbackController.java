/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.feedback.RequestFeedbackDto;
import com.mytutor.services.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 *
 * @author Nguyen Van Dat
 */
@RestController
@RequestMapping("/api")
public class FeedbackController {

    @Autowired
    
    private FeedbackService feedbackService; 
    
    @GetMapping("/reviews")
    public ResponseEntity<?> getAllReviews(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return feedbackService.getAllReviews(pageNo, pageSize);
    }

    @GetMapping("/tutors/{tutorId}/reviews")
    public ResponseEntity<?> getReviewsByTutorId(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @PathVariable int tutorId
    ) {
        return feedbackService.getReviewsByTutorId(pageNo, pageSize, tutorId);
    }

    @GetMapping("/tutors/{tutorId}/reviews/{reviewId}")
    public ResponseEntity<?> getReviewById(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @PathVariable int tutorId,
            @PathVariable int reviewId
    ) {
        return feedbackService.getReviewById(pageNo, pageSize, tutorId, reviewId);
    }

    @PostMapping("/tutors/{tutorId}/reviews")
    public ResponseEntity<?> createReview(
            Principal principal,
            @PathVariable int tutorId,
            @Valid @RequestBody RequestFeedbackDto requestFeedbackDto
    ) {
        return feedbackService.createReview(principal, tutorId, requestFeedbackDto);
    }

    @PutMapping("/tutors/{tutorId}/reviews/{reviewId}")
    public ResponseEntity<?> updateReviewById(
            @PathVariable int tutorId,
            @PathVariable int reviewId,
            @Valid @RequestBody RequestFeedbackDto requestFeedbackDto
    ) {
        return feedbackService.updateReviewById(tutorId, reviewId, requestFeedbackDto);
    }

    @DeleteMapping("/tutors/{tutorId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReviewById(
            @PathVariable int tutorId,
            @PathVariable int reviewId
    ) {
        return feedbackService.deleteReviewById(tutorId, reviewId);
    }
}
