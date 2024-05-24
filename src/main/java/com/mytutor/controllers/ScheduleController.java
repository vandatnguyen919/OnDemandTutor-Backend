package com.mytutor.controllers;

import com.mytutor.dto.InputTimeslotDto;
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

    @PostMapping("/tutors/{tutorId}/add-new-schedule")
    public ResponseEntity<?> addNewSchedule(
            @PathVariable Integer tutorId,
            @RequestBody List<InputTimeslotDto> tutorScheduleDto) {
        return scheduleService.addNewSchedule(tutorId, tutorScheduleDto);
    }

    @GetMapping("/{tutorId}")
    public ResponseEntity<?> getAllSchedules(@PathVariable Integer tutorId) {
        return scheduleService.getSchedulesByTutorId(tutorId);
    }
}
