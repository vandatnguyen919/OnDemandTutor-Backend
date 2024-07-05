package com.mytutor.dto.timeslot;

import lombok.Data;

import java.sql.Time;

/**
 *
 * @author vothimaihoa
 */
@Data
public class RequestTimeslotDto {
    private Time startTime;

    private Time endTime;

    private Integer dayOfWeek;

}
