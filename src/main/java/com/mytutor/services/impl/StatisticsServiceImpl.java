package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.statistics.SubjectTuitionSum;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.services.StatisticsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final AppointmentRepository appointmentRepository;

    public StatisticsServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<SubjectTuitionSum> getTotalTuitionBySubject() {
        return appointmentRepository.findTotalTuitionBySubject(AppointmentStatus.PAID);
    }
}
