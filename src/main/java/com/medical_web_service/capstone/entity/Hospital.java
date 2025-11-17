package com.medical_web_service.capstone.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String businessName;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String department;

    @Column(length = 100)
    private String type;

    @Column(length = 50)
    private String status;

    private Double x;
    private Double y;
}
