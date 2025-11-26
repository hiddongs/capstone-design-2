package com.medical_web_service.capstone.controller;

import com.medical_web_service.capstone.entity.SearchingDiseaseHistory;
import com.medical_web_service.capstone.service.SearchingDiseaseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/searching-disease-history")
@RequiredArgsConstructor
public class SearchingDiseaseHistoryController {

    private final SearchingDiseaseHistoryService searchingDiseaseHistoryService;

    // ⭐ 검색 기록 저장
    @PostMapping("/{userId}")
    public ResponseEntity<SearchingDiseaseHistory> storeDisease(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "diseaseName") String diseaseName
    ) {
        SearchingDiseaseHistory savedHistory =
                searchingDiseaseHistoryService.storeDisease(userId, diseaseName);
        return ResponseEntity.ok(savedHistory);
    }

    // ⭐ 사용자별 검색 기록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<SearchingDiseaseHistory>> getHistoryByUser(
            @PathVariable(name = "userId") Long userId
    ) {
        List<SearchingDiseaseHistory> historyList =
                searchingDiseaseHistoryService.getHistoryByUser(userId);
        return ResponseEntity.ok(historyList);
    }

    // ⭐ 사용자별 특정 검색 기록 삭제
    @DeleteMapping("/{userId}/{historyId}")
    public ResponseEntity<Void> deleteHistory(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "historyId") Long historyId
    ) {
        searchingDiseaseHistoryService.deleteHistoryByIdAndUser(userId, historyId);
        return ResponseEntity.noContent().build();
    }
}
