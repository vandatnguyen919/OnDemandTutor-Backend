/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.constants.FeedbackType;
import com.mytutor.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Nguyen Van Dat
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Query("SELECT f FROM Feedback f WHERE f.type = :type")
    Page<Feedback> findFeedbackByType(@Param("type") FeedbackType type, Pageable pageable);
}
