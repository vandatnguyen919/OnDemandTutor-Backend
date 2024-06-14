package com.mytutor.dto.timeslot;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author vothimaihoa
 *
 */
@Data
public class ResponseTimeslotDto {
    
    private int id;

    private Integer accountId;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer dayOfWeek;

    private LocalDate scheduleDate;

    private boolean isOccupied;

    private Integer appointmentId;
}
