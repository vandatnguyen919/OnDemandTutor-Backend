package com.mytutor.repositories;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.entities.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.mytutor.constants.AppointmentStatus.PAID;

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
    Timeslot findTimeslotWithDateAndWeeklySchedule(@Param("weeklyScheduleId") Integer weeklyScheduleId,
                                                   @Param("scheduleDate") LocalDate scheduleDate);

}
