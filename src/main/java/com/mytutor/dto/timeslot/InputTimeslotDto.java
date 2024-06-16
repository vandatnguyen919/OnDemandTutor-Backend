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
    private Time startTime;

    private Time endTime;

    private Integer dayOfWeek;

}
