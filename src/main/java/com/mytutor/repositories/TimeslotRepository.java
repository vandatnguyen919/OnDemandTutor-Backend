package com.mytutor.repositories;

import com.mytutor.entities.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {
    
//    @Query("SELECT t FROM Timeslot t " +
//            "WHERE t.account.id = :tutorId " +
//            "AND t.scheduleDate BETWEEN :startDate AND :endDate " +
//            " AND t.isOccupied = false" +
//            " AND t.dayOfWeek = :dayOfWeek " +
//            " ORDER BY t.scheduleDate, t.startTime ASC")
//    List<Timeslot> findByTutorIdAndDayOfWeekAndDateRange(@Param("tutorId") Integer tutorId,
//                                                         @Param("startDate") LocalDate startDate,
//                                                         @Param("endDate") LocalDate endDate,
//                                                         @Param("dayOfWeek") Integer dayOfWeek);

    // tìm timeslot có weeklyid = w.getId và isOccupied = true
    @Query(
            "SELECT t FROM Timeslot t WHERE t.weeklySchedule.id = :weeklyScheduleId AND t.isOccupied = true"
    )
    Timeslot findOccupiedTimeslotByWeeklySchedule(@Param("weeklyScheduleId") Integer weeklyScheduleId);
}
