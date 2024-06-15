/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mytutor.entities;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.Role;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Nguyen Van Dat
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "Account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 1000)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    private Date dateOfBirth;

    private Boolean gender; // male: false, female: true

    private String address;

    @Column(unique = true)
    private String phoneNumber;

    private String avatarUrl;

    private Date createdAt;

    private String description;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE; // Default: active

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "tutor_subject",
            joinColumns = @JoinColumn(name = "tutor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id", referencedColumnName = "id"))
    private Set<Subject> subjects = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tutor_detail_id")
    private TutorDetail tutorDetail;

    @OneToMany(mappedBy = "account")
    private Set<Education> educations;

    @OneToMany(mappedBy = "tutor")
    private Set<Feedback> feedbacks;
}
