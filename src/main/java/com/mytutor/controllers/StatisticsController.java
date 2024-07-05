package com.mytutor.controllers;

import com.mytutor.dto.LessonStatisticDto;
import com.mytutor.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author vothimaihoa
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    AppointmentService appointmentService;

    // lay ra so lieu ve appointment cua mot student
    @GetMapping("/{studentId}/learn-statistics")
    public ResponseEntity<LessonStatisticDto> getStudentLearntStatistic(@PathVariable Integer studentId) {
        return appointmentService.getStudentStatistics(studentId);
    }

    @GetMapping("/{tutorId}/teach-statistics")
    public ResponseEntity<LessonStatisticDto> getTutorTaughtStatistic(@PathVariable Integer tutorId) {
        return appointmentService.getTutorStatistics(tutorId);
    }

    @GetMapping("/{tutorId}/salary")
    public ResponseEntity<Double> getTutorSalaryStatistic
            (@PathVariable Integer tutorId,
             @RequestParam Integer month,
             @RequestParam Integer year) {
        return ResponseEntity.status(HttpStatus.OK).body(appointmentService.getTutorSalary(tutorId,month,year));
    }
}
