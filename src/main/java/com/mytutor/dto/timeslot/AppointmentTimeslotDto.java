package com.mytutor.dto.timeslot;

import com.mytutor.entities.Timeslot;
import com.mytutor.entities.WeeklySchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentTimeslotDto {
    private int id;

    private Time startTime;

    private Time endTime;

    private String scheduleDate;

    public static AppointmentTimeslotDto mapToDto(Timeslot timeslot) {
        String scheduleDate = new SimpleDateFormat("yyyy-MM-dd")
                .format(Date.valueOf(timeslot.getScheduleDate()));
        return new AppointmentTimeslotDto(
                timeslot.getId(),
                timeslot.getWeeklySchedule().getStartTime(),
                timeslot.getWeeklySchedule().getEndTime(),
                scheduleDate
        );
    }
}
