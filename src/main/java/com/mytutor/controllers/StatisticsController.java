package com.mytutor.controllers;

import com.mytutor.constants.Role;
import com.mytutor.dto.statistics.DateTuitionSum;
import com.mytutor.dto.statistics.LessonStatisticDto;
import com.mytutor.dto.statistics.SubjectTuitionSum;
import com.mytutor.dto.statistics.SubjectTutorCount;
import com.mytutor.services.AppointmentService;
import com.mytutor.services.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Get total tuition sum by subject or date",
            description = "Retrieve the total tuition sum grouped by either subject or date based on the query parameter." +
                    "Suggestion: 'subject' is often used in bar charts and 'date' is often used in line charts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the total tuition sum"),
            @ApiResponse(responseCode = "204", description = "No content available for the provided query parameter")
    })
    @GetMapping("/tuition-sum")
    public ResponseEntity<?> getTotalTuition(
            @Parameter(in = ParameterIn.QUERY, description = "Specify the grouping criteria: 'subject' or 'date'", required = true)
            @RequestParam(value = "queryBy") String query
    ) {
        if (query.equalsIgnoreCase("subject")) {
            List<SubjectTuitionSum> subjectTuitionSums = statisticsService.getTotalTuitionBySubject();
            return ResponseEntity.status(HttpStatus.OK).body(subjectTuitionSums);
        } else if (query.equalsIgnoreCase("date")) {
            List<DateTuitionSum> dateTuitionSums = statisticsService.getTotalTuitionByDate();
            return ResponseEntity.status(HttpStatus.OK).body(dateTuitionSums);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/tutor-count-by-subject")
    public ResponseEntity<?> countTutorsBySubject() {
        List<SubjectTutorCount> subjectTutorCounts = statisticsService.countTutorsBySubject();
        return ResponseEntity.status(HttpStatus.OK).body(subjectTutorCounts);
    }

    @GetMapping("/count-by-role")
    public ResponseEntity<?> countByRole(
            @RequestParam(value = "role", required = false) Role role
    ) {        return statisticsService.countAccountsByRole(role);
    }
}
