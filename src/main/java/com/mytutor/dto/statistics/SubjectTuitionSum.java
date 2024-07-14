package com.mytutor.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectTuitionSum {
    private String subjectName;
    private Double totalTuition;
}
