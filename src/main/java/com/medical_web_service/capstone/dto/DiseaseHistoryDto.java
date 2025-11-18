package com.medical_web_service.capstone.dto;

import com.medical_web_service.capstone.entity.DiseaseHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseHistoryDto {

    private Long id;
    private String diseaseName;
    private String completeCureOrNot;
    private String dateOnOnset;
    private Long userId;

    public DiseaseHistoryDto(DiseaseHistory entity) {
        this.id = entity.getId();
        this.diseaseName = entity.getDiseaseName();
        this.completeCureOrNot = entity.getCompleteCureOrNot();
        this.dateOnOnset = entity.getDateOnOnset();
        this.userId = entity.getUser().getId();
    }
}
