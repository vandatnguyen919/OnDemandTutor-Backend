/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.QuestionDto;
import com.mytutor.dto.LessonStatisticDto;
import com.mytutor.services.AppointmentService;
import com.mytutor.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Nguyen Van Dat
 */
@RestController
@RequestMapping("/api/")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AppointmentService appointmentService;

//    @Hidden
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents(
        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
        @RequestParam(value = "status", required = false) String status
        ) {
        return studentService.getAllStudents(pageNo, pageSize, status);
    }


    @GetMapping("/questions")
    public ResponseEntity<?> getAllQuestions(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "type", defaultValue = "all", required = false) String type,
            @RequestParam(value = "subjects", defaultValue = "all", required = false) String subjects,
            @RequestParam(value = "questionContent", defaultValue = "", required = false) String questionContent) {
        return studentService.getAllQuestion(pageNo, pageSize, type, subjects, questionContent);
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<?> getQuestionById(
            @PathVariable("questionId") int questionId) {
        return studentService.getQuestionById(questionId);
    }

    @PostMapping("/students/{studentId}/questions")
    public ResponseEntity<?> addQuestion(
            @PathVariable Integer studentId,
            @RequestBody QuestionDto questionDto) {
        return studentService.addQuestion(studentId, questionDto);
    }

    @PutMapping("/students/{studentId}/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Integer studentId,
            @PathVariable Integer questionId,
            @RequestBody QuestionDto questionDto) {
        return studentService.updateQuestion(studentId, questionId, questionDto);
    }

    @DeleteMapping("/students/{studentId}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable Integer studentId,
            @PathVariable Integer questionId) {
        return studentService.deleteQuestion(studentId, questionId);
    }

}
