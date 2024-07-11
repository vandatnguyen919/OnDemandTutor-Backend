package com.mytutor.dto.statistics;

import com.mytutor.dto.SubjectDto;
import com.mytutor.entities.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vothimaihoa
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonStatisticDto {
    int accountId;

    // total
    int totalLessons;
    int totalLearntTutor; // for student
    int totalTaughtStudent; // for tutor
    double totalIncome;
    List<SubjectDto> totalSubjects = new ArrayList<>();

    // monthly
    int thisMonthLessons;
    int thisMonthTutor; // for student
    int thisMonthStudent; // for tutor
    double totalMonthlyIncome;
    List<SubjectDto> thisMonthSubjects = new ArrayList<>();
}
