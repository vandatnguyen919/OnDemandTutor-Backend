package com.mytutor.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TutorIncomeDto {
    private Integer tutorId;
    private String fullName;
    private Double totalTuition;
    private Integer percentage;
    private Double totalTutorIncome;
    private Double totalProfit;
    private Long totalAppointment;
}
