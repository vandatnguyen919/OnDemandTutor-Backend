package com.mytutor.controllers;

import com.mytutor.dto.statistics.LessonStatisticDto;
import com.mytutor.dto.statistics.SubjectTuitionSum;
import com.mytutor.services.AppointmentService;
import com.mytutor.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private StatisticsService statisticsService;

    // lay ra so lieu ve appointment cua mot student
    @GetMapping("/{studentId}/learn-statistics")
    public ResponseEntity<LessonStatisticDto> getStudentLearntStatistic(@PathVariable Integer studentId) {
        return appointmentService.getStudentStatistics(studentId);
    }

    @GetMapping("/{tutorId}/teach-statistics")
    public ResponseEntity<LessonStatisticDto> getTutorTaughtStatistic(@PathVariable Integer tutorId) {
        return appointmentService.getTutorStatistics(tutorId);
    }

    @GetMapping("/tuition-sum")
    public List<SubjectTuitionSum> getTotalTuitionBySubject() {
        return statisticsService.getTotalTuitionBySubject();
    }
}
