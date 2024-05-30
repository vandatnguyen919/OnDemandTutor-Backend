package com.mytutor.controllers;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vothimaihoa
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired


    @GetMapping("/{tutorId}")
    public ResponseEntity<?> getAppoinmentByTutorId(@PathVariable String tutorId) {
        return
    }

}
