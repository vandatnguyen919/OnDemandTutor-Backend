package com.mytutor.services;

import com.mytutor.dto.InputTimeslotDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Service
public interface ScheduleService {
    ResponseEntity<?> addNewSchedule(Integer tutorId, List<InputTimeslotDto> tutorScheduleDto);
    ResponseEntity<?> getSchedulesByTutorId(Integer tutorId);
    ResponseEntity<?> updateTimeslotStatus(Integer tutorId, Integer timeslotId, Boolean status);
}
