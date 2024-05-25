package com.mytutor.controllers;

import com.mytutor.dto.TimeSlot.InputTimeslotDto;
import com.mytutor.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    // allow tutor role only
    @PostMapping("/tutors/{tutorId}/add-new-schedule")
    public ResponseEntity<?> addNewSchedule(
            @PathVariable Integer tutorId,
            @RequestBody List<InputTimeslotDto> tutorScheduleDto,
            @RequestParam(defaultValue = "1", required = false) Integer numberOfWeeks ) {
        return scheduleService.addNewSchedule(tutorId, tutorScheduleDto, numberOfWeeks);
    }

    // everyone
    @GetMapping("/{tutorId}")
    public ResponseEntity<?> getNext7DaysSchedulesOfATutor(
            @PathVariable Integer tutorId) {
        return scheduleService.getNext7DaysSchedulesOfByTutorId(tutorId);
    }

}
