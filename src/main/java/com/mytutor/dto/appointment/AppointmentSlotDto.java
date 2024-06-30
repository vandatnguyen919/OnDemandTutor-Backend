package com.mytutor.dto.appointment;

import com.mytutor.dto.timeslot.TimeslotInAppointmentDto;
import com.mytutor.entities.Timeslot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 *
 * @author vothimaihoa
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentSlotDto {
    private int timeslotId;

    private Time startTime;

    private Time endTime;

    private String scheduleDate;

    private int dayOfWeek;

    private ResponseAppointmentWithoutTimeslotDto appointment;

    public static AppointmentSlotDto mapToDto(Timeslot timeslot) {
        String scheduleDate = new SimpleDateFormat("yyyy-MM-dd")
                .format(Date.valueOf(timeslot.getScheduleDate()));
        return new AppointmentSlotDto(
                timeslot.getId(),
                timeslot.getWeeklySchedule().getStartTime(),
                timeslot.getWeeklySchedule().getEndTime(),
                scheduleDate,
                timeslot.getWeeklySchedule().getDayOfWeek(),
                ResponseAppointmentWithoutTimeslotDto.mapToDto(timeslot.getAppointment())
        );
    }

}
