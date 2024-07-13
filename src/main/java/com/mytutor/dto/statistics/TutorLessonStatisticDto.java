package com.mytutor.dto.statistics;

import com.mytutor.dto.SubjectDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TutorLessonStatisticDto {
    int accountId;

    // total
    int totalLessons;
    int totalTaughtStudent;
    double totalIncome;
    List<SubjectDto> totalSubjects = new ArrayList<>();
}
