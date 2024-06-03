/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.constants.VerifyStatus;
import com.mytutor.entities.Certificate;
import java.util.List;

import com.mytutor.entities.Education;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vothimaihoa
 */
@Repository
@Transactional
public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    List<Certificate> findByAccountId(Integer tutorId);

    @Modifying
    @Query("DELETE FROM Certificate c WHERE c.account.id = :accountId")
    void deleteCertificateByTutorId(@Param("accountId") Integer tutorId);

    @Modifying
    @Query("UPDATE Certificate c SET c.verifyStatus = :status WHERE c.account.id = :accountId")
    void updateCertificateByTutorId(
            @Param("status") VerifyStatus status,
            @Param("accountId") Integer tutorId);
}
