package com.mytutor.dto.timeslot;

import com.mytutor.entities.Timeslot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeslotWithoutAppointmentDto {
    private int id;

    private Time startTime;

    private Time endTime;

    private String scheduleDate;

    private int dayOfWeek;

    public static TimeslotWithoutAppointmentDto mapToDto(Timeslot timeslot) {
        String scheduleDate = new SimpleDateFormat("yyyy-MM-dd")
                .format(Date.valueOf(timeslot.getScheduleDate()));
        return new TimeslotWithoutAppointmentDto(
                timeslot.getId(),
                timeslot.getWeeklySchedule().getStartTime(),
                timeslot.getWeeklySchedule().getEndTime(),
                scheduleDate,
                timeslot.getWeeklySchedule().getDayOfWeek()
        );
    }
}
