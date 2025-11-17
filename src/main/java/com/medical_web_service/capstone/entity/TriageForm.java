package com.medical_web_service.capstone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "triage_form")
public class TriageForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String department;

    private String departmentName;

    // ⭐ JSON 저장을 위한 TEXT or JSON 타입
    @Column(columnDefinition = "TEXT")
    private String detailJson;

    // AI가 생성한 요약
    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    private LocalDateTime createdAt;
}
