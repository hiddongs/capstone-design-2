package com.medical_web_service.capstone.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medical_web_service.capstone.dto.TriageRequestDto;
import com.medical_web_service.capstone.entity.TriageForm;
import com.medical_web_service.capstone.service.TriageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/triage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // 필요 시 프론트 도메인만 제한 가능
public class TriageController {

    private final TriageService triageService;

    /**
     * 📌 문진 제출 API
     * 프론트에서 submit 시 호출됨
     */
    @PostMapping("/submit")
    public ResponseEntity<TriageForm> submitTriage(@RequestBody TriageRequestDto dto) {
        try {
            TriageForm saved = triageService.submit(dto);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 📌 특정 유저 문진 기록 조회
     * GET /api/triage/user/3
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TriageForm>> getUserTriage(@PathVariable Long userId) {
        List<TriageForm> list = triageService.getUserForms(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * 📌 전체 문진 기록 조회 (관리자, 의사용)
     * GET /api/triage/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<TriageForm>> getAllTriage() {
        return ResponseEntity.ok(triageService.getAllForms());
    }
}