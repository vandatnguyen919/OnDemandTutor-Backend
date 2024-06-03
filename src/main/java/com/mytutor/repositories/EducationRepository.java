/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.constants.VerifyStatus;
import com.mytutor.entities.Certificate;
import com.mytutor.entities.Education;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author vothimaihoa
 */
@Repository
@Transactional
public interface EducationRepository extends JpaRepository<Education, Integer> {

    @Query("SELECT e FROM Education e WHERE e.account.id = :accountId ORDER BY e.degreeType DESC")
    List<Education> findByAccountId(@Param("accountId") Integer tutorId);

    @Modifying
    @Query("DELETE FROM Education e WHERE e.account.id = :accountId")
    void deleteEducationByTutorId(@Param("accountId") Integer tutorId);

    @Modifying
    @Query("UPDATE Education e SET e.verifyStatus = :status WHERE e.account.id = :accountId")
    void updateEducationByTutorId(
            @Param("status") VerifyStatus status,
            @Param("accountId") Integer tutorId);


}
