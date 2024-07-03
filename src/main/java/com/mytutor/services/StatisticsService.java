package com.mytutor.services;

import com.mytutor.dto.statistics.DateTuitionSum;
import com.mytutor.dto.statistics.SubjectTuitionSum;

import java.util.List;

public interface StatisticsService {

    List<SubjectTuitionSum> getTotalTuitionBySubject();

    List<DateTuitionSum> getTotalTuitionByDate();
}
