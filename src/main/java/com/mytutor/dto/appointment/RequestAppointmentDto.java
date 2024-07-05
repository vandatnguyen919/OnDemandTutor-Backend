package com.mytutor.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAppointmentDto {

    private Integer id;

    private LocalDateTime createdAt;

    private String description;

    private Integer tutorId;

    private Integer studentId;
}
