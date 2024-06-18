/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mytutor.repositories;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.Role;
import com.mytutor.entities.Account;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nguyen Van Dat
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Page<Account> findByRole(Role role, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.role = :role AND a.status = :status")
    Page<Account> findRoleAndStatus(@Param("role") Role role, @Param("status") AccountStatus status, Pageable pageable);
}
