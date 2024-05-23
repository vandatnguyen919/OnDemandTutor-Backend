/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.entities.Account;
import com.mytutor.entities.TutorDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author vothimaihoa
 */
@Repository
public interface TutorDetailRepository extends JpaRepository<TutorDetail, Integer> {
    TutorDetail findByAccountId(Integer accountId);
}
