package com.mytutor.repositories;

import com.mytutor.entities.Account;
import com.mytutor.entities.Timeslot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {
    Page<Timeslot> findByAccountId(Integer tutorId, Pageable pageable);
}
