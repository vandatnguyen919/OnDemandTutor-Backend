/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mytutor.dto.timeslot;

import com.mytutor.entities.Timeslot;
import java.sql.Time;
import java.time.LocalTime;

import com.mytutor.entities.WeeklySchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

/**
 *
 * @author Nguyen Van Dat
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeslotDto {

    private int id;
    
    private LocalTime startTime;

    private LocalTime endTime;
    
    private boolean isOccupied;
    
    public static TimeslotDto mapToDto(WeeklySchedule timeslot) {
        return new TimeslotDto(
            timeslot.getId(),
            timeslot.getStartTime(),
            timeslot.getEndTime(),
            timeslot.isOccupied()
        );
    }
}
