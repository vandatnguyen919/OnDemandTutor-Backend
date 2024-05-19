/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */

@Entity
@NoArgsConstructor
@Data
@Table(name = "Tutor_Detail")
public class Tutor {

    @Id
    private int tutorId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "tutor_id")
    private Account account;

    @Column(name = "teaching_price_per_hour")
    private Double teachingPricePerHour;

    @Column(name = "background_description")
    private String backgroundDescription;

    @Column(name = "meeting_link")
    private String meetingLink;
    
    @Column (name= "video_introduction_link")
    private String videoIntroductionLink;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "Tutor_Subject",
               joinColumns = @JoinColumn(name = "tutor_id"),
               inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Role> subjects = new HashSet<>();

}

