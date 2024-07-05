package com.mytutor.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestReScheduleDto {
    int oldTimeslotId;
    int newWeeklyScheduleId;
}
