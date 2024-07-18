package com.mytutor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppointmentReportDto {

    private int id;

    private String createdAt;

    private String studentFullName;

    private String tutorFullName;

    private double appointmentTuition;

    private double tutorIncome;

    private double tutorFeePercentage;

    private double totalProfit;
}
