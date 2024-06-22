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
    int totalLessons;
    int totalLearntTutor;
    int totalTaughtStudent;
    List<Subject> subjects = new ArrayList<>();
}
