package com.mytutor.repositories;

import com.mytutor.entities.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.List;

@Repository
@Transactional
public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Integer> {
    @Query("SELECT w FROM WeeklySchedule w " +
            "WHERE w.account.id = :tutorId " +
            "AND w.dayOfWeek = :dayOfWeek AND w.isUsing = true " +
            "AND ((w.startTime >= :startTime AND w.startTime < :endTime) " +
            "OR (w.endTime > :startTime AND w.endTime <= :endTime) " +
            "OR (w.startTime <= :startTime AND w.endTime >= :endTime))")
    List<WeeklySchedule> findOverlapUsingSchedule(@Param("tutorId") Integer tutorId,
                                                  @Param("dayOfWeek") Integer dayOfWeek,
                                                  @Param("startTime") Time startTime,
                                                  @Param("endTime") Time endTime);

    @Query("SELECT w FROM WeeklySchedule w " +
            " WHERE w.account.id = :tutorId " +
            " AND w.isUsing = true " +
            " AND w.dayOfWeek = :dayOfWeek ")
    List<WeeklySchedule> findByTutorIdAnDayOfWeek(@Param("tutorId")Integer tutorId,
                                                  @Param("dayOfWeek")Integer dayOfWeek);

    @Query("SELECT w FROM WeeklySchedule w " +
            " WHERE w.account.id = :tutorId " +
            " AND w.isUsing = false " +
            " AND w.dayOfWeek = :dayOfWeek AND w.startTime = :startTime AND w.endTime = :endTime ")
    WeeklySchedule findNotUsingSlotByTutor(@Param("tutorId")Integer tutorId,
                                           @Param("dayOfWeek") Integer dayOfWeek,
                                           @Param("startTime") Time startTime,
                                           @Param("endTime") Time endTime);

    @Query("SELECT w FROM WeeklySchedule w " +
            " WHERE w.account.id = :tutorId " )
    List<WeeklySchedule> findByTutorId(@Param("tutorId")Integer tutorId);

    @Modifying
    @Query("DELETE FROM WeeklySchedule w WHERE w.account.id = :accountId")
    void deleteScheduleByTutorId(@Param("accountId") Integer accountId);

}
