package com.mytutor.services;

import com.mytutor.dto.timeslot.InputWeeklyScheduleDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface ScheduleService {
    ResponseEntity<?> addNewSchedule(Integer tutorId, List<InputWeeklyScheduleDto> tutorScheduleDto);
//    ResponseEntity<?> getNext7DaysSchedulesByTutorId(Integer tutorId);
    ResponseEntity<?> updateScheduleStatus(Integer tutorId, Integer scheduleId, Boolean status);
    ResponseEntity<?> removeSchedule(Integer tutorId, Integer scheduleId);
    ResponseEntity<?> getTutorWeeklySchedule(Integer tutorId);
}
