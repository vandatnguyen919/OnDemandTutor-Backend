package com.mytutor.services;

import com.mytutor.dto.statistics.SubjectTuitionSum;

import java.util.List;

public interface StatisticsService {

    List<SubjectTuitionSum> getTotalTuitionBySubject();
}
