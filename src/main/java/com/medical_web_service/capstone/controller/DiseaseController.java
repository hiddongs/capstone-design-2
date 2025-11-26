package com.medical_web_service.capstone.controller;

import com.medical_web_service.capstone.dto.DiseaseInfo;
import com.medical_web_service.capstone.service.DiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    private final DiseaseService diseaseService;

    @Autowired
    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    // 특정 질병의 정보를 반환하는 엔드포인트
    @GetMapping("/{diseaseName}")
    public ResponseEntity<DiseaseInfo> getDiseaseInfo(
            @PathVariable(name = "diseaseName") String diseaseName
    ) {
        DiseaseInfo diseaseInfo = diseaseService.getDiseaseInfo(diseaseName);
        if (diseaseInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(diseaseInfo);
    }

    // 질병 증상을 로드하는 엔드포인트 (관리자 또는 초기화 용도)
    @PostMapping("/load-symptoms")
    public ResponseEntity<String> loadDiseaseSymptoms() {
        diseaseService.loadDiseaseSymptoms();
        return ResponseEntity.ok("Symptoms data loaded successfully.");
    }

    // 전체 질병 리스트를 반환하는 엔드포인트
    @GetMapping
    public ResponseEntity<List<DiseaseInfo>> getAllDiseases() {
        List<DiseaseInfo> allDiseases = diseaseService.getAllDiseases();
        return ResponseEntity.ok(allDiseases);
    }
}
