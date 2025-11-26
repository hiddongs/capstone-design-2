package com.medical_web_service.capstone.controller;

import com.medical_web_service.capstone.dto.DiseaseHistoryDto;
import com.medical_web_service.capstone.service.UserDiseaseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disease-history")
@RequiredArgsConstructor
public class DiseaseHistoryController {

    private final UserDiseaseHistoryService userDiseaseHistoryService;

    // 질병 이력 추가
    @PostMapping("/user/{userId}")
    public ResponseEntity<DiseaseHistoryDto> addDiseaseHistory(
            @PathVariable(name = "userId") Long userId,
            @RequestBody DiseaseHistoryDto diseaseHistoryDTO) {

        DiseaseHistoryDto createdHistory =
                userDiseaseHistoryService.addDiseaseHistory(userId, diseaseHistoryDTO);

        return ResponseEntity.ok(createdHistory);
    }

    // 특정 사용자의 질병 이력 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DiseaseHistoryDto>> getDiseaseHistoriesByUserId(
            @PathVariable(name = "userId") Long userId) {

        List<DiseaseHistoryDto> histories =
                userDiseaseHistoryService.getDiseaseHistoriesByUserId(userId);

        return ResponseEntity.ok(histories);
    }

    // 질병 이력 수정
    @PutMapping("/{userId}/{id}")
    public ResponseEntity<DiseaseHistoryDto> updateDiseaseHistory(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "id") Long id,
            @RequestBody DiseaseHistoryDto diseaseHistoryDTO) {

        DiseaseHistoryDto updatedHistory =
                userDiseaseHistoryService.updateDiseaseHistory(userId, id, diseaseHistoryDTO);

        return ResponseEntity.ok(updatedHistory);
    }

    // 질병 이력 삭제
    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteDiseaseHistory(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "id") Long id) {

        userDiseaseHistoryService.deleteDiseaseHistory(userId, id);

        return ResponseEntity.noContent().build();
    }

    // 모든 질병 이력 조회 (관리자용)
    @GetMapping("/all")
    public ResponseEntity<List<DiseaseHistoryDto>> getAllDiseaseHistories() {

        List<DiseaseHistoryDto> allHistories =
                userDiseaseHistoryService.getAllDiseaseHistories();

        return ResponseEntity.ok(allHistories);
    }
}
