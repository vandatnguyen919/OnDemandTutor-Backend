package com.mytutor.repositories;

import com.mytutor.entities.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author vothimaihoa
 */
public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {
    List<Timeslot> findByAccountId(Integer tutorId);
}
