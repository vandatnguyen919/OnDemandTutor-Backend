package com.mytutor.repositories;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.entities.Account;
import com.mytutor.entities.Appointment;
import com.mytutor.entities.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    @Query("SELECT a FROM Appointment a " +
            " WHERE (a.tutor.id = :accountId OR a.student.id = :accountId)" +
            " AND (:status is null OR a.status = :status)")
    Page<Appointment> findAppointmentByAccountId(Integer accountId, AppointmentStatus status, Pageable pageable);

    @Query("SELECT a FROM Appointment a "
            + " WHERE :status is null OR a.status = :status")
    Page<Appointment> findAppointments(AppointmentStatus status, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.status = :status AND a.student.id = :studentId")
    List<Appointment> findAppointmentsWithPendingPayment(@Param("studentId") Integer studentId,
                                                         @Param("status") AppointmentStatus status);

    // rollback automatically after 30 minutes
    List<Appointment> findByStatusAndCreatedAtBefore(AppointmentStatus status, LocalDateTime dateTime);

    @Query("SELECT count(a.id) FROM Appointment a WHERE (a.student.id = :id OR a.tutor.id = :id) AND " +
            "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR a.createdAt < :endDate)")
    int findNoOfTotalAppointmentsByStudentOrTutorId(@Param("id") Integer id,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT distinct a.student FROM Appointment a WHERE a.tutor.id = :id AND " +
            "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR a.createdAt < :endDate)")
    List<Account> findTotalTaughtStudents(@Param("id") Integer id,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT distinct a.tutor FROM Appointment a WHERE a.student.id = :id AND " +
            "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR a.createdAt < :endDate)")
    List<Account> findTotalLearntTutors(@Param("id") Integer id,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT distinct a.subject FROM Appointment a WHERE (a.student.id = :id OR a.tutor.id = :id) AND " +
            "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR a.createdAt < :endDate)")
    List<Subject> findTotalSubjects(@Param("id") Integer id,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);


}
