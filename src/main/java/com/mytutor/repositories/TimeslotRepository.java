package com.mytutor.repositories;

import com.mytutor.entities.Account;
import com.mytutor.entities.Timeslot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {

    @Query("SELECT t FROM Timeslot t " +
            "WHERE t.account.id = :tutorId " +
            "AND t.scheduleDate > :currentDate AND t.scheduleDate <= :endDate " +
            "ORDER BY t.scheduleDate, t.startTime ASC")
    List<Timeslot> findByTutorIdOrderedByScheduleDate(@Param("tutorId") Integer tutorId,
                                                      @Param("currentDate") LocalDate currentDate,
                                                      @Param("endDate")LocalDate endDate);

    @Query("SELECT t FROM Timeslot t " +
            "WHERE t.account.id = :tutorId " +
            "AND t.scheduleDate = :date " +
            "AND t.startTime = :startTime AND t.endTime = :endTime")
    Timeslot findAnExistedTimeslot(@Param("tutorId") Integer tutorId,
                                   @Param("date") LocalDate date,
                                   @Param("startTime") Time startTime,
                                   @Param("endTime") Time endTime);
}
