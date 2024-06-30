/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.mytutor.services;

import com.mytutor.dto.student.QuestionDto;
import com.mytutor.dto.student.RequestQuestionDto;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Nguyen Van Dat
 */
public interface StudentService {

    ResponseEntity<?> getAllStudents(int pageNo, int pageSize, String status);
    
    ResponseEntity<?> getAllQuestion(int pageNo, int pageSize, String type, String subjects, String content);

    ResponseEntity<?> getQuestionById(Integer questionId);

    ResponseEntity<?> addQuestion(Integer studentId, RequestQuestionDto requestQuestionDto);
    
    ResponseEntity<?> updateQuestion(Integer studentId, Integer questionId, RequestQuestionDto requestQuestionDto);
    
    ResponseEntity<?> deleteQuestion(Integer studentId, Integer questionId);
}
