package com.mytutor.dto.timeslot;

import lombok.Data;

import java.time.LocalTime;

/**
 *
 * @author vothimaihoa
 */
@Data
public class InputWeeklyScheduleDto {
    private LocalTime startTime;

    private LocalTime endTime;

    private Integer dayOfWeek;

}
