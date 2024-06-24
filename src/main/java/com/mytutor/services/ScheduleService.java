package com.mytutor.services;

import com.mytutor.dto.timeslot.RequestWeeklyScheduleDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface ScheduleService {
    ResponseEntity<?> addNewSchedule(Integer tutorId, List<RequestWeeklyScheduleDto> tutorScheduleDto);
//    ResponseEntity<?> getNext7DaysSchedulesByTutorId(Integer tutorId);
    ResponseEntity<?> updateSchedule(Integer tutorId, List<RequestWeeklyScheduleDto> newSchedules);
//    ResponseEntity<?> removeSchedule(Integer tutorId, Integer scheduleId);
    ResponseEntity<?> getTutorWeeklySchedule(Integer tutorId);
}
