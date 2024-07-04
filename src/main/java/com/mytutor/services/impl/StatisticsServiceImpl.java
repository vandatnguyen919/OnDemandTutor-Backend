package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.statistics.DateTuitionSum;
import com.mytutor.dto.statistics.SubjectTuitionSum;
import com.mytutor.dto.statistics.SubjectTutorCount;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.repositories.SubjectRepository;
import com.mytutor.services.StatisticsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final AppointmentRepository appointmentRepository;

    private final SubjectRepository subjectRepository;

    public StatisticsServiceImpl(AppointmentRepository appointmentRepository, SubjectRepository subjectRepository) {
        this.appointmentRepository = appointmentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<SubjectTuitionSum> getTotalTuitionBySubject() {
        return appointmentRepository.findTotalTuitionBySubject(AppointmentStatus.PAID);
    }

    @Override
    public List<DateTuitionSum> getTotalTuitionByDate() {
        return appointmentRepository.findTotalTuitionByDate(AppointmentStatus.PAID);
    }

    @Override
    public List<SubjectTutorCount> countTutorsBySubject() {
        return subjectRepository.countTutorsBySubject();
    }
}
