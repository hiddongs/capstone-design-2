package com.medical_web_service.capstone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "searching_disease_history")
public class SearchingDiseaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자가 입력한 증상
    @Column(nullable = false, length = 1000)
    private String symptom;

    // GPT가 추론한 질병명 (최상위 추론)
    @Column(nullable = true)
    private String diseaseName;

    // AI 전체 응답 저장 (옵션)
    @Column(columnDefinition = "TEXT")
    private String aiResult;

    // 퍼지 위험도 점수 (0.0~1.0)
    private Double severityScore;

    // 퍼지 위험 레벨 (낮음 / 보통 / 높음)
    private String severityLevel;

    // 기록 생성 시간
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
