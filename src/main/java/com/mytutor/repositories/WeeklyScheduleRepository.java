package com.mytutor.repositories;

import com.mytutor.entities.WeeklySchedule;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Integer> {
    @Query("SELECT w FROM WeeklySchedule w " +
            "WHERE w.account.id = :tutorId " +
            "AND w.dayOfWeek = :dayOfWeek " +
            "AND ((w.startTime >= :startTime AND w.startTime < :endTime) " +
            "OR (w.endTime > :startTime AND w.endTime <= :endTime) " +
            "OR (w.startTime <= :startTime AND w.endTime >= :endTime))")
    List<WeeklySchedule> findOverlapSchedule(@Param("tutorId") Integer tutorId,
                                       @Param("dayOfWeek") Integer dayOfWeek,
                                       @Param("startTime") LocalTime startTime,
                                       @Param("endTime") LocalTime endTime);

    @Query("SELECT w FROM WeeklySchedule w " +
            " WHERE w.account.id = :tutorId " +
            " AND w.isUsing = true " +
            " AND w.dayOfWeek = :dayOfWeek " +
            " AND NOT (w.startTime < :startTime AND w.dayOfWeek = :currentDayOfWeek) ")
    List<WeeklySchedule> findByTutorIdAnDayOfWeek(@Param("tutorId")Integer tutorId,
                                                  @Param("dayOfWeek")Integer dayOfWeek,
                                                  @Param("startTime")LocalTime startTime,
                                                  @Param("currentDayOfWeek")Integer currentDayOfWeek);

    @Query("SELECT w FROM WeeklySchedule w " +
            " WHERE w.account.id = :tutorId " )
    List<WeeklySchedule> findByTutorId(@Param("tutorId")Integer tutorId);

}
