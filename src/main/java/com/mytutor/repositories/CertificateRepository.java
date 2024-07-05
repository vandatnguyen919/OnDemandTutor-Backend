/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.constants.VerifyStatus;
import com.mytutor.entities.Certificate;
import java.util.List;
import java.util.Optional;

import com.mytutor.entities.Education;

import com.mytutor.entities.Education;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Override
    Optional<Certificate> findById(Integer id);

    @Query("SELECT c FROM Certificate c WHERE c.account.id = :accountId AND c.isVerified = :isVerified")
    List<Certificate> findByAccountId(@Param("accountId") Integer tutorId, @Param("isVerified") boolean isVerified);

    @Modifying
    @Query("DELETE FROM Certificate c WHERE c.account.id = :accountId")
    void deleteCertificateByTutorId(@Param("accountId") Integer tutorId);

}
