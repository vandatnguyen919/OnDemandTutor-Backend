package com.mytutor.repositories;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.entities.Timeslot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author vothimaihoa
 */
@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {

    @Query(
            "SELECT t FROM Timeslot t WHERE t.weeklySchedule.id = :weeklyScheduleId " +
                    "AND t.scheduleDate = :scheduleDate"
    )
    Timeslot findByDateAndWeeklySchedule(@Param("weeklyScheduleId") Integer weeklyScheduleId,
                                         @Param("scheduleDate") LocalDate scheduleDate);

    @Query(
            "SELECT t FROM Timeslot t " +
                    "WHERE (t.appointment.student.id = :accountId) " +
                    "AND t.appointment.status = :status " +
                    "AND (t.scheduleDate > :currentDate " +
                    "     OR (t.scheduleDate = :currentDate AND t.weeklySchedule.startTime > :currentTime)) " +
                    "ORDER BY t.scheduleDate ASC, t.weeklySchedule.startTime ASC"
    )
    Page<Timeslot> findUpcomingTimeslotByStudent(@Param("accountId") Integer accountId,
                                               @Param("status") AppointmentStatus status,
                                               @Param("currentDate") LocalDate currentDate,
                                               @Param("currentTime") LocalTime currentTime,
                                               Pageable pageable);
    @Query(
            "SELECT t FROM Timeslot t " +
                    " WHERE (t.appointment.student.id = :accountId) " +
                    " AND (t.scheduleDate < :currentDate " +
                    "     OR (t.scheduleDate = :currentDate AND t.weeklySchedule.startTime <= :currentTime)) " +
                    " ORDER BY t.scheduleDate DESC, t.weeklySchedule.startTime DESC"
    )
    Page<Timeslot> findPastTimeslotByStudent(@Param("accountId") Integer accountId,
                                             @Param("currentDate") LocalDate currentDate,
                                             @Param("currentTime") LocalTime currentTime,
                                             Pageable pageable);


    @Query(
            "SELECT t FROM Timeslot t " +
                    "WHERE (t.appointment.tutor.id = :accountId) " +
                    "AND t.appointment.status = :status " +
                    "AND (t.scheduleDate > :currentDate " +
                    "     OR (t.scheduleDate = :currentDate AND t.weeklySchedule.startTime > :currentTime)) " +
                    "ORDER BY t.scheduleDate ASC, t.weeklySchedule.startTime ASC"
    )
    Page<Timeslot> findUpcomingTimeslotByTutor(@Param("accountId") Integer accountId,
                                               @Param("status") AppointmentStatus status,
                                               @Param("currentDate") LocalDate currentDate,
                                               @Param("currentTime") LocalTime currentTime,
                                               Pageable pageable);
    @Query(
            "SELECT t FROM Timeslot t " +
                    " WHERE (t.appointment.tutor.id = :accountId)" +
                    " AND (t.scheduleDate < :currentDate " +
                    " OR (t.weeklySchedule.startTime <= :currentTime AND t.scheduleDate = :currentDate))" +
                    " ORDER BY t.scheduleDate DESC, t.weeklySchedule.startTime DESC"
    )
    Page<Timeslot> findPastTimeslotByTutor(@Param("accountId") Integer accountId,
                                           @Param("currentDate") LocalDate currentDate,
                                           @Param("currentTime") LocalTime currentTime,
                                           Pageable pageable);



}
