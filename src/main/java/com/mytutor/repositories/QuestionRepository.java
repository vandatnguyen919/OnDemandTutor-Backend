/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mytutor.repositories;

import com.mytutor.constants.QuestionStatus;
import com.mytutor.entities.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Nguyen Van Dat
 */
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    
    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);
}
