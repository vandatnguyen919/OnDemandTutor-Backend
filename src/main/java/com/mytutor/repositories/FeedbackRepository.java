/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.constants.FeedbackType;
import com.mytutor.entities.Account;
import com.mytutor.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * @author Nguyen Van Dat
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Query("SELECT f FROM Feedback f WHERE f.type = :type ORDER BY f.createdAt DESC, f.rating DESC")
    Page<Feedback> findFeedbackByType(@Param("type") FeedbackType type, Pageable pageable);

    @Query("SELECT f FROM Feedback f WHERE f.type = :type AND f.tutor = :tutor ORDER BY f.createdAt DESC, f.rating DESC")
    Page<Feedback> findFeedbackByTypeAndTutorId(@Param("type") FeedbackType type, @Param("tutor") Account tutor, Pageable pageable);

    @Query("SELECT ROUND(AVG(f.rating), 1) FROM Feedback f WHERE f.tutor = :account")
    Double getAverageRatingByAccount(@Param("account") Account account);

    List<Feedback> findByTutorAndCreatedBy(Account tutor, Account createdBy);
}
