package com.mytutor.controllers;

import com.mytutor.dto.timeslot.RequestWeeklyScheduleDto;
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
    @PostMapping("/tutors/{tutorId}/timeslots")
    public ResponseEntity<?> addNewSchedule(
            @PathVariable Integer tutorId,
            @RequestBody List<RequestWeeklyScheduleDto> tutorScheduleDto) {
        return scheduleService.addNewSchedule(tutorId, tutorScheduleDto);
    }

    // everyone
    @GetMapping("/tutors/{tutorId}")
    public ResponseEntity<?> getNext7DaysSchedulesOfATutor(
            @PathVariable Integer tutorId) {
        return scheduleService.getTutorWeeklySchedule(tutorId);
    }

//    @DeleteMapping("{scheduleId}/tutors/{tutorId}")
//    public ResponseEntity<?> deleteSchedule(
//            @PathVariable Integer scheduleId,
//            @PathVariable Integer tutorId) {
//        return scheduleService.removeSchedule(tutorId, scheduleId);
//    }
//
    @PutMapping("tutors/{tutorId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Integer tutorId,
            @RequestBody List<RequestWeeklyScheduleDto> newTutorScheduleDto) {
        return scheduleService.updateSchedule(tutorId, newTutorScheduleDto);
    }

}
