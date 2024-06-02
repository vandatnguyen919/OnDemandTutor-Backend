package com.mytutor.dto;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.timeslot.ResponseTimeslotDto;
import com.mytutor.dto.timeslot.TimeslotDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private Integer id;
    private LocalDateTime createdAt;
    private String description;
    private AppointmentStatus status;
    private Integer tutorId;
    private Integer studentId;
    private List<Integer> timeslotIds = new ArrayList<>();
}

