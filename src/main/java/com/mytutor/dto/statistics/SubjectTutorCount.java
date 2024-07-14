package com.mytutor.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SubjectTutorCount {
    private String subjectName;
    private Long tutorCount;
}
