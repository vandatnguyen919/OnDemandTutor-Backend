package com.mytutor.dto;

import com.mytutor.constants.AppointmentStatus;
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
public class InputAppointmentDto {

    private String description;

    private Integer tutorId;

    private List<Integer> timeslotIds = new ArrayList<>();
}

