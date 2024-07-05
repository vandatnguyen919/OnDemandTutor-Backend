package com.mytutor.services;

import com.mytutor.constants.Role;
import com.mytutor.dto.statistics.DateTuitionSum;
import com.mytutor.dto.statistics.SubjectTuitionSum;
import com.mytutor.dto.statistics.SubjectTutorCount;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StatisticsService {

    List<SubjectTuitionSum> getTotalTuitionBySubject();

    List<DateTuitionSum> getTotalTuitionByDate();

    List<SubjectTutorCount> countTutorsBySubject();

    ResponseEntity<?> countAccountsByRole(Role role);
}
