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

    private String businessName;
    private String address;
    private String roadAddress;
    private String phone;

    private String department;         // 기존 진료과
    private String departmentName;     // 진료과목내용명 (더 상세)

    private String type;               // 기존 의료기관종별명
    private String medicalType;        // 확장된 의료기관종별명
    @Column(length = 50)
    private String statusDetail;  // 상세영업상태명

    private Integer doctorCount;       // 의료인수
    private Integer roomCount;         // 입원실수
    private Integer bedCount;          // 병상수

    private String status;             // 영업 상태

    private Double x;
    private Double y;
}
