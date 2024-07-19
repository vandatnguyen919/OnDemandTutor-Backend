package com.mytutor.services;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.AppointmentReportDto;
import com.mytutor.dto.appointment.AppointmentSlotDto;
import com.mytutor.dto.appointment.InputAppointmentDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.appointment.RequestReScheduleDto;
import com.mytutor.dto.appointment.ResponseAppointmentDto;
import com.mytutor.dto.statistics.StudentLessonStatisticDto;
import com.mytutor.dto.statistics.StudentProfitDto;
import com.mytutor.dto.statistics.TutorIncomeDto;
import com.mytutor.dto.statistics.TutorLessonStatisticDto;
import com.mytutor.entities.Appointment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vothimaihoa
 */
@Service
public interface AppointmentService {
    ResponseAppointmentDto getAppointmentById(Integer appointmentId);

    PaginationDto<ResponseAppointmentDto> getAppointmentsByAccountId(Integer tutorId, AppointmentStatus status, Integer pageNo, Integer pageSize);

    ResponseEntity<?> createAppointment(Integer studentId, InputAppointmentDto appointment);

    ResponseEntity<?> updateAppointmentStatus(Integer tutorId, Integer appointmentId, String status);

    ResponseEntity<?> rollbackAppointment(int appointmentId);

    void rollbackAppointment(Appointment appointment);

    ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointments(AppointmentStatus status, Integer pageNo, Integer pageSize);

    ResponseEntity<StudentLessonStatisticDto> getStudentStatistics(Integer studentId);
  
    TutorLessonStatisticDto getTutorStatistics(Integer accountId, Integer month, Integer year);
  
    ResponseEntity<ResponseAppointmentDto> updateAppointmentSchedule(int appointmentId, RequestReScheduleDto dto);
  
    ResponseEntity<AppointmentSlotDto> cancelSlotsInAppointment(int accountId, int timeslotId);

    void sendCreateBookingEmail(int appointmentId);

    List<AppointmentReportDto> getAllAppointmentReports();

    List<StudentProfitDto> getStudentProfits();

    List<TutorIncomeDto> getTutorIncomes();
}
