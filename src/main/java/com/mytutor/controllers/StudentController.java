/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Nguyen Van Dat
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @GetMapping("")
    public ResponseEntity<?> getAllStudents() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @PostMapping("/post-a-question")
    public ResponseEntity<?> postQuestion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
