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

    @PostMapping("/tutors/{tutorId}/add-new-schedule")
    public ResponseEntity<?> addNewSchedule(
            @PathVariable Integer tutorId,
            @RequestBody List<InputTimeslotDto> tutorScheduleDto) {
        return scheduleService.addNewSchedule(tutorId, tutorScheduleDto);
    }

    @GetMapping("/{tutorId}")
    public ResponseEntity<?> getAllSchedulesOfATutor(
            @PathVariable Integer tutorId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "7", required = false) int pageSize) {
        return scheduleService.getSchedulesByTutorId(tutorId, pageNo, pageSize);
    }
}
