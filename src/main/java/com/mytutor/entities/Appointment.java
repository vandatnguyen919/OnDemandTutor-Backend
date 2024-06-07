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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "Appointment_Timeslot",
            joinColumns = @JoinColumn(name = "appointment_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "timeslot_id", referencedColumnName = "id"))
    private List<Timeslot> timeslots = new ArrayList<>();

    @Column(name = "tuition")
    private Double tuition;

    @OneToMany(mappedBy = "appointment")
    List<Payment> payments = new ArrayList<>();

    private String meetingLink;

}

