package com.mytutor.entities;

import com.mytutor.constants.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vothimaihoa
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "Appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "rescheduled_at")
    private LocalDateTime rescheduledAt;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "tutor_id")
    private Account tutor;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_id")
    private Account student;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timeslot> timeslots = new ArrayList<>();

    @Column(name = "tuition")
    private Double tuition;

    @OneToMany(mappedBy = "appointment")
    List<Payment> payments = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    private String meetingLink;

}

