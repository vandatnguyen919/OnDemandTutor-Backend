package com.mytutor.dto.statistics;

import com.mytutor.entities.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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
    Set<Subject> totalSubjects = new HashSet<>();

    // monthly
    int thisMonthLessons;
    int thisMonthTutor; // for student
    int thisMonthStudent; // for tutor
    double totalMonthlyIncome;
    Set<Subject> thisMonthSubjects = new HashSet<>();
}
