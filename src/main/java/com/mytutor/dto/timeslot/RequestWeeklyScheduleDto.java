package com.mytutor.dto.timeslot;

import lombok.Data;

import java.sql.Time;

/**
 *
 * @author vothimaihoa
 */
@Data
public class RequestWeeklyScheduleDto {
    private Time startTime;

    private Time endTime;

    private Integer dayOfWeek;

    private boolean isUsing;

}
