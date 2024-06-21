package com.mytutor.dto.timeslot;

import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

/**
 *
 * @author vothimaihoa
 */
@Data
public class InputWeeklyScheduleDto {
    private Time startTime;

    private Time endTime;

    private Integer dayOfWeek;

    private boolean isUsing;

}
