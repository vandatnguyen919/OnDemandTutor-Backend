package com.mytutor.dto.timeslot;

import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

/**
 *
 * @author vothimaihoa
 */
@Data
public class InputTimeslotDto {
    private LocalTime startTime;

    private LocalTime endTime;

    private Integer dayOfWeek;

}
