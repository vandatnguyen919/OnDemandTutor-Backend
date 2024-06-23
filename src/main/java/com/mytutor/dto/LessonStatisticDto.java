package com.mytutor.dto;

import com.mytutor.entities.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    List<Subject> totalSubjects = new ArrayList<>();

    // monthly
    int thisMonthLessons;
    int thisMonthTutor; // for student
    int thisMonthStudent; // for tutor
    List<Subject> thisMonthSubjects = new ArrayList<>();
}
