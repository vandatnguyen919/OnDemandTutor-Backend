package com.mytutor.dto.statistics;

import com.mytutor.dto.SubjectDto;
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
public class StudentLessonStatisticDto {
    int accountId;

    // total
    int totalLessons;
    int totalLearntTutor; // for student
    List<SubjectDto> totalSubjects = new ArrayList<>();

    // monthly
    int thisMonthLessons;
    int thisMonthTutor; // for student
    List<SubjectDto> thisMonthSubjects = new ArrayList<>();
}
