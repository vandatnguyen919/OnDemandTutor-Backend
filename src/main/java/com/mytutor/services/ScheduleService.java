package com.mytutor.services;

import com.mytutor.dto.timeslot.InputWeeklyScheduleDto;
import com.mytutor.entities.WeeklySchedule;
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
    ResponseEntity<?> updateSchedule(Integer tutorId, List<InputWeeklyScheduleDto> newSchedules);
//    ResponseEntity<?> removeSchedule(Integer tutorId, Integer scheduleId);
    ResponseEntity<?> getTutorWeeklySchedule(Integer tutorId);
}
