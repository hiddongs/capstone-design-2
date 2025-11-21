package com.medical_web_service.capstone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private String reservedTime;

    @Column(nullable = false)
    private String department;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(nullable = true)
    private Long triageId;
}
