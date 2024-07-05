/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.QuestionStatus;
import com.mytutor.constants.Role;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.student.QuestionDto;
import com.mytutor.dto.ResponseAccountDetailsDto;
import com.mytutor.dto.student.RequestQuestionDto;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
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
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> getAllStudents(int pageNo, int pageSize, String status) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Account> students;
        if (status == null || status.isBlank()) {
            students = accountRepository.findByRole(Role.STUDENT, pageable);
        }
        else {
            students = accountRepository.findByRoleAndStatus(
                Role.STUDENT,
                AccountStatus.valueOf(status.toUpperCase()),
                pageable);
        }
        List<Account> studentList = students.getContent();

        List<ResponseAccountDetailsDto> content = studentList.stream()
                .map(s -> ResponseAccountDetailsDto.mapToDto(s)).toList();
        PaginationDto<ResponseAccountDetailsDto> studentListResponseDto = new PaginationDto<>();
        studentListResponseDto.setContent(content);
        studentListResponseDto.setPageNo(students.getNumber());
        studentListResponseDto.setPageSize(students.getSize());
        studentListResponseDto.setTotalElements(students.getTotalElements());
        studentListResponseDto.setTotalPages(students.getTotalPages());
        studentListResponseDto.setLast(students.isLast());

        return ResponseEntity.status(HttpStatus.OK).body(studentListResponseDto);
    }

    @Override
    public ResponseEntity<?> getAllQuestion(int pageNo, int pageSize, String type, String subjects, String questionContent) {
        Set<String> subjectSet = subjects.equalsIgnoreCase("all") ? null
                : Arrays.stream(subjects.split("[,\\s+]+"))
                .map(s -> s.trim().toLowerCase()).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Question> questions = questionRepositoryCustom.findQuestionsByFilter(
                null,
                type.equalsIgnoreCase("all") ? null :QuestionStatus.valueOf(type.toUpperCase()),
                subjectSet,
                questionContent,
                pageable);
        return getResponseEntity(questions);
    }

    @Override
    public ResponseEntity<?> getQuestionById(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found"));
        QuestionDto questionDto = QuestionDto.mapToDto(question, question.getSubject().getSubjectName());
        return ResponseEntity.status(HttpStatus.OK).body(questionDto);
    }

    @Override
    public ResponseEntity<?> addQuestion(Integer studentId, RequestQuestionDto requestQuestionDto) {

        Account student = accountRepository.findById(studentId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!student.getRole().equals(Role.STUDENT)) {
            throw new AccountNotFoundException("Only student can create questions!");
        }

        if (questionRepository.countByAccountAndDate(studentId, LocalDate.now()) == 3) {
            throw new QuestionNotFoundException("You have reached your daily limit - only 3 questions can be created each day!");
        }

        Subject subject = subjectRepository.findBySubjectName(requestQuestionDto.getSubjectName())
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));

        Question question = new Question();
        question.setTitle(requestQuestionDto.getTitle());
        question.setContent(requestQuestionDto.getContent());
        question.setQuestionUrl(requestQuestionDto.getQuestionUrl());
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
    public ResponseEntity<?> updateQuestion(Integer studentId, Integer questionId, RequestQuestionDto requestQuestionDto) {

        Account student = accountRepository.findById(studentId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found"));

        if (student.getId() != question.getAccount().getId()) {
            throw new QuestionNotFoundException("Question does not belong to this account");
        }

        Subject subject = null;
        if (requestQuestionDto.getSubjectName() != null) {
            subject = subjectRepository.findBySubjectName(requestQuestionDto.getSubjectName())
                    .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        }

        if (requestQuestionDto.getTitle() != null) {
            question.setTitle(requestQuestionDto.getTitle());
        }
        if (requestQuestionDto.getContent() != null) {
            question.setContent(requestQuestionDto.getContent());
        }
        if (requestQuestionDto.getQuestionUrl() != null) {
            question.setQuestionUrl(requestQuestionDto.getQuestionUrl());
        }
        if (subject != null) {
            question.setSubject(subject);
        }
        question.setModifiedAt(new Date());
        question.setStatus(QuestionStatus.PROCESSING);

        Question updatedQuestion = questionRepository.save(question);

        QuestionDto questionResponse = QuestionDto.mapToDto(updatedQuestion, updatedQuestion.getSubject().getSubjectName());

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

    @Override
    public ResponseEntity<?> getAllQuestionsByStudent(int studentId, int pageNo,
                                                      int pageSize, String status, String subjects) {
        Account student = accountRepository.findById(studentId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
        Set<String> subjectSet = subjects.equalsIgnoreCase("all") ? null
                : Arrays.stream(subjects.split("[,\\s+]+"))
                .map(s -> s.trim().toLowerCase()).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Question> questions = questionRepositoryCustom.findQuestionsByFilter(
                student,
                status.equalsIgnoreCase("all") ? null :QuestionStatus.valueOf(status.toUpperCase()),
                subjectSet,
                null,
                pageable);
        return getResponseEntity(questions);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(Page<Question> questions) {
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

}
