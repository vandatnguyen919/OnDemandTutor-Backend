package com.mytutor.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private Integer id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Account tutor;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Account student;

//    @OneToMany(mappedBy="appointment")
//    private Set<Timeslot> timeslots = new HashSet<>();

    public enum AppointmentStatus {
        PROCESSING,
        CONFIRMED,
        CANCELED,
        SUCCESS,
        FAILED
    }
}

