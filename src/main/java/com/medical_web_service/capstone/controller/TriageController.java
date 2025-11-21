package com.medical_web_service.capstone.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
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
     * 문진 제출 API
     * 프론트에서 submit 시 호출됨
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitTriage(@RequestBody TriageRequestDto dto) {
        try {
            // 1) 서비스에서 저장 + AI 요약까지 처리
            TriageForm saved = triageService.submit(dto);

            // 2) 클라이언트에 보낼 JSON 객체 구성
            Map<String, Object> response = new HashMap<>();
            response.put("triageId", saved.getId());
            response.put("userId", saved.getUserId());
            response.put("department", saved.getDepartment());
            response.put("departmentName", saved.getDepartmentName());
            response.put("createdAt", saved.getCreatedAt());
            response.put("aiSummary", saved.getAiSummary());

            // detailJson → 다시 파싱해서 answers 배열로 돌려주기
            List<Map<String, Object>> answers =
                    new ObjectMapper().readValue(saved.getDetailJson(), List.class);

            response.put("answers", answers);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("문진 저장 실패");
        }
    }

    /**
     * 특정 유저 문진 기록 조회
     * GET /api/triage/user/3
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TriageForm>> getUserTriage(@PathVariable("id") Long userId) {
        List<TriageForm> list = triageService.getUserForms(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * 전체 문진 기록 조회 (관리자, 의사용)
     * GET /api/triage/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<TriageForm>> getAllTriage() {
        return ResponseEntity.ok(triageService.getAllForms());
    }
    
    @PostMapping("/assign-doctor")
    public ResponseEntity<?> assignDoctor(@RequestBody Map<String, Long> req) {

        Long triageId = req.get("triageId");
        Long doctorId = req.get("doctorId");

        TriageForm form = triageService.assignDoctor(triageId, doctorId);

        return ResponseEntity.ok(form);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<TriageForm>> getDoctorTriage(@PathVariable Long doctorId) {
        List<TriageForm> list = triageService.getDoctorForms(doctorId);
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{triageId}")
    public ResponseEntity<?> getTriageDetail(@PathVariable Long triageId) {

        TriageForm form = triageService.getById(triageId);

        if (form == null) {
            return ResponseEntity.status(404).body("해당 문진을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(form);
    }

}