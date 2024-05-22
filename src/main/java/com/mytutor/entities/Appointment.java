package com.mytutor.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    public enum AppointmentStatus {
        PROCESSING,
        CONFIRMED,
        CANCELED,
        SUCCESS,
        FAILED
    }
}

