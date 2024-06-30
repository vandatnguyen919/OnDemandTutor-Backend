/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mytutor.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */
@Entity
@NoArgsConstructor
@Table(name = "Tutor_Detail")
@Data
public class TutorDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "teaching_price_per_hour")
    private Double teachingPricePerHour;

    @Column(name = "background_description", length = 1000)
    private String backgroundDescription;

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "video_introduction_link")
    private String videoIntroductionLink;

    @Column(name = "percentage")
    private Integer percentage = 15;

}
