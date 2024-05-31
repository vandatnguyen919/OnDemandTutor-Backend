/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import com.mytutor.dto.QuestionDto;
import com.mytutor.services.StudentService;
import io.swagger.v3.oas.annotations.Hidden;
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

/**
 *
 * @author Nguyen Van Dat
 */
@RestController
@RequestMapping("/api/")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Hidden
    @GetMapping("")
    public ResponseEntity<?> getAllStudents() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("/questions")
    public ResponseEntity<?> getAllQuestion(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "type", defaultValue = "all", required = false) String type) {
        return studentService.getAllQuestion(pageNo, pageSize, type);
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
