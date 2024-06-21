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
public class ResponseAppointmentDto {
    private Integer id;

    private LocalDateTime createdAt;

    private String description;

    private String subjectName;

    private AppointmentStatus status;

    private Integer tutorId;

    private Integer studentId;

    private double tuition;

    private List<Integer> timeslotIds = new ArrayList<>();
}
