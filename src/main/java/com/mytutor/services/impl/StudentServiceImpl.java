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
import com.mytutor.entities.Subject;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.QuestionNotFoundException;
import com.mytutor.exceptions.SubjectNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.QuestionRepository;
import com.mytutor.repositories.QuestionRepositoryCustom;
import com.mytutor.repositories.SubjectRepository;
import com.mytutor.services.StudentService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
public class StudentServiceImpl implements StudentService {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionRepositoryCustom questionRepositoryCustom;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Override
    public ResponseEntity<?> getAllQuestion(int pageNo, int pageSize, String type, String subjects, String questionContent) {
        Set<String> subjectSet = subjects.equalsIgnoreCase("all") ? null
                : Arrays.stream(subjects.split("[,\\s+]+"))
                .map(s -> s.trim().toLowerCase()).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Question> questions = questionRepositoryCustom.findQuestionsByFilter(
                type.equalsIgnoreCase("all") ? null :QuestionStatus.valueOf(type.toUpperCase()),
                subjectSet,
                questionContent,
                pageable);
        List<Question> listOfQuestions = questions.getContent();

        List<QuestionDto> content = listOfQuestions.stream()
                .map(q -> QuestionDto.mapToDto(q, q.getSubject().getSubjectName())).toList();

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
    public ResponseEntity<?> getQuestionById(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found"));
        QuestionDto questionDto = QuestionDto.mapToDto(question, question.getSubject().getSubjectName());
        return ResponseEntity.status(HttpStatus.OK).body(questionDto);
    }

    @Override
    public ResponseEntity<?> addQuestion(Integer studentId, QuestionDto questionDto) {

        Account student = accountRepository.findById(studentId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Subject subject = subjectRepository.findBySubjectName(questionDto.getSubjectName()).orElseThrow(() -> new SubjectNotFoundException("Subject not found"));

        Question question = new Question();
        question.setTitle(questionDto.getTitle());
        question.setContent(questionDto.getContent());
        question.setQuestionUrl(questionDto.getQuestionUrl());
        question.setCreatedAt(new Date());
        question.setModifiedAt(new Date());
        question.setStatus(QuestionStatus.PROCESSING);
        question.setSubject(subject);
        question.setAccount(student);

        Question newQuestion = questionRepository.save(question);

        QuestionDto questionResponse = QuestionDto.mapToDto(newQuestion, subject.getSubjectName());

        return ResponseEntity.status(HttpStatus.OK).body(questionResponse);
    }

    @Override
    public ResponseEntity<?> updateQuestion(Integer studentId, Integer questionId, QuestionDto questionDto) {

        Account student = accountRepository.findById(studentId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Subject subject = subjectRepository.findBySubjectName(questionDto.getSubjectName())
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found"));

        if (student.getId() != question.getAccount().getId()) {
            throw new QuestionNotFoundException("Question does not belong to this account");
        }

        question.setTitle(questionDto.getTitle());

        question.setContent(questionDto.getContent());
        question.setQuestionUrl(questionDto.getQuestionUrl());
        question.setModifiedAt(new Date());
        question.setStatus(QuestionStatus.PROCESSING);
        question.setSubject(subject);

        Question updatedQuestion = questionRepository.save(question);

        QuestionDto questionResponse = QuestionDto.mapToDto(updatedQuestion, subject.getSubjectName());

        return ResponseEntity.status(HttpStatus.OK).body(questionResponse);
    }

    @Override
    public ResponseEntity<?> deleteQuestion(Integer studentId, Integer questionId) {

        Account student = accountRepository.findById(studentId).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new QuestionNotFoundException("Question not found"));

        if (student.getId() != question.getAccount().getId()) {
            throw new QuestionNotFoundException("Question does not belong to this account");
        }
        
        questionRepository.delete(question);
        
        return ResponseEntity.status(HttpStatus.OK).body("Question deleted");
    }

}
