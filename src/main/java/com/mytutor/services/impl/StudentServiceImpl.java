/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.QuestionStatus;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.QuestionDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Question;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.QuestionRepository;
import com.mytutor.services.StudentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Nguyen Van Dat
 */
public class StudentServiceImpl implements StudentService {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public ResponseEntity<?> getAllQuestion(int pageNo, int pageSize, String type) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Question> questions;
        if (type.equalsIgnoreCase(QuestionStatus.SOLVED.toString())) {
            questions = questionRepository.findByStatus(QuestionStatus.SOLVED, pageable);
        } else if (type.equalsIgnoreCase(QuestionStatus.UNSOLVED.toString())) {
            questions = questionRepository.findByStatus(QuestionStatus.UNSOLVED, pageable);
        } else {
            questions = questionRepository.findAll(pageable);
        }
        List<Question> listOfQuestions = questions.getContent();

        List<QuestionDto> content = listOfQuestions.stream().map(q -> QuestionDto.mapToDto(q)).toList();

        PaginationDto<QuestionDto> questionResponseDto = new PaginationDto<>();
        questionResponseDto.setContent(content);
        questionResponseDto.setPageNo(questions.getNumber());
        questionResponseDto.setPageSize(questions.getSize());
        questionResponseDto.setTotalElements(questions.getTotalElements());
        questionResponseDto.setTotalPages(questions.getTotalPages());
        questionResponseDto.setLast(questions.isLast());

        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @Override
    public ResponseEntity<?> addQuestion(Integer studentId, QuestionDto questionDto) {

        Account student = accountRepository.findById(studentId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody

    }

    @Override
    public ResponseEntity<?> updateQuestion(Integer studentId, Integer questionId, QuestionDto questionDto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ResponseEntity<?> deleteQuestion(Integer studentId, Integer questionId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
