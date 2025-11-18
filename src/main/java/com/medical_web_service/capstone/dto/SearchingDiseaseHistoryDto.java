package com.medical_web_service.capstone.dto;

import com.medical_web_service.capstone.entity.SearchingDiseaseHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchingDiseaseHistoryDto {

    private Long id;
    private String symptom;
    private String diseaseName;
    private String aiResult;
    private Double severityScore;
    private String severityLevel;
    private String createdAt;  // LocalDateTime → String

    private Long userId;

    public SearchingDiseaseHistoryDto(SearchingDiseaseHistory entity) {
        this.id = entity.getId();
        this.symptom = entity.getSymptom();
        this.diseaseName = entity.getDiseaseName();
        this.aiResult = entity.getAiResult();
        this.severityScore = entity.getSeverityScore();
        this.severityLevel = entity.getSeverityLevel();
        this.createdAt = entity.getCreatedAt().toString();  
        this.userId = entity.getUser().getId();
    }
}
