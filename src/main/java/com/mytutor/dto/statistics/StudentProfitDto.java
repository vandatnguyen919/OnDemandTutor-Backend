package com.mytutor.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentProfitDto {
    private Integer studentId;
    private String fullName;
    private Double totalTuition;
    private Double totalProfit;
    private Long totalAppointment;
}
