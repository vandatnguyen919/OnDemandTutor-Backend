/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mytutor.repositories;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.DegreeType;
import com.mytutor.constants.QuestionStatus;
import com.mytutor.entities.Account;
import com.mytutor.entities.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Nguyen Van Dat
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("SELECT COUNT(q) FROM Question q WHERE q.account.id = :accountId AND DATE(q.createdAt) = :date")
    long countByAccountAndDate(@Param("accountId") Integer accountId, @Param("date") LocalDate date);
}
