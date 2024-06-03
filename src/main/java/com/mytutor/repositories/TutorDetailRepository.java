/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.entities.TutorDetail;
import java.util.Optional;
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
public interface TutorDetailRepository extends JpaRepository<TutorDetail, Integer> {
    Optional<TutorDetail> findByAccountId(Integer accountId);

    @Modifying
    @Query("DELETE FROM TutorDetail WHERE account.id = :accountId")
    void deleteByAccountId(@Param("accountId") Integer accountId);
}
