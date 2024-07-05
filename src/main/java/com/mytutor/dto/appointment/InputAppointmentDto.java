package com.mytutor.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String subjectName;

    private Integer tutorId;

    private List<Integer> timeslotIds = new ArrayList<>();
}

