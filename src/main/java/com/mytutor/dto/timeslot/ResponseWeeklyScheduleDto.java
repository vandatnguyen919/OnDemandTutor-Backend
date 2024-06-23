package com.mytutor.dto.timeslot;

import com.mytutor.entities.WeeklySchedule;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author vothimaihoa
 *
 */
@Data
public class ResponseWeeklyScheduleDto {
    
    private int id;

    private Integer accountId;

    private Time startTime;

    private Time endTime;

    private Integer dayOfWeek;

    private boolean isUsing;


    public static ResponseWeeklyScheduleDto mapToDto(WeeklySchedule weeklySchedule) {
        if (weeklySchedule == null) {
            return null;
        }
        ResponseWeeklyScheduleDto dto = new ResponseWeeklyScheduleDto();
        dto.setId(weeklySchedule.getId());
        dto.setAccountId(weeklySchedule.getAccount().getId());
        dto.setStartTime(weeklySchedule.getStartTime());
        dto.setEndTime(weeklySchedule.getEndTime());
        dto.setDayOfWeek(weeklySchedule.getDayOfWeek());
        dto.setUsing(weeklySchedule.isUsing());

        return dto;
    }
}
