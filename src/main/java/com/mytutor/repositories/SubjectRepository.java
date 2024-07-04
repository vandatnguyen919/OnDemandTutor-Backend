/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.dto.statistics.SubjectTutorCount;
import com.mytutor.entities.Subject;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vothimaihoa
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer>{

    Optional<Subject> findBySubjectName(String subjectName);

    @Query("SELECT new com.mytutor.dto.statistics.SubjectTutorCount(s.subjectName, COUNT(DISTINCT a.id)) " +
            "FROM Subject s " +
            "LEFT JOIN s.accounts a " +
            "GROUP BY s.subjectName")
    List<SubjectTutorCount> countTutorsBySubject();
}
