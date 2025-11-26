package com.medical_web_service.capstone.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical_web_service.capstone.dto.TriageRequestDto;
import com.medical_web_service.capstone.entity.TriageForm;
import com.medical_web_service.capstone.service.TriageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/triage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TriageController {

    private final TriageService triageService;

    /**
     * 문진 제출 API
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitTriage(@RequestBody TriageRequestDto dto) {
        try {
            TriageForm saved = triageService.submit(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("triageId", saved.getId());
            response.put("userId", saved.getUserId());
            response.put("department", saved.getDepartment());
            response.put("departmentName", saved.getDepartmentName());
            response.put("createdAt", saved.getCreatedAt());
            response.put("aiSummary", saved.getAiSummary());

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
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TriageForm>> getUserTriage(
            @PathVariable(name = "userId") Long userId
    ) {
        List<TriageForm> list = triageService.getUserForms(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * 전체 문진 기록 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<TriageForm>> getAllTriage() {
        return ResponseEntity.ok(triageService.getAllForms());
    }

    /**
     * 의사 배정
     */
    @PostMapping("/assign-doctor")
    public ResponseEntity<?> assignDoctor(@RequestBody Map<String, Long> req) {

        Long triageId = req.get("triageId");
        Long doctorId = req.get("doctorId");

        TriageForm form = triageService.assignDoctor(triageId, doctorId);

        return ResponseEntity.ok(form);
    }

    /**
     * 의사별 문진 정보 조회
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<TriageForm>> getDoctorTriage(
            @PathVariable(name = "doctorId") Long doctorId
    ) {
        List<TriageForm> list = triageService.getUserForms(doctorId);
        return ResponseEntity.ok(list);
    }

    /**
     * 문진 상세 조회
     */
    @GetMapping("/{triageId}")
    public ResponseEntity<?> getTriageDetail(
            @PathVariable(name = "triageId") Long triageId
    ) {

        TriageForm form = triageService.getById(triageId);

        if (form == null) {
            return ResponseEntity.status(404).body("해당 문진을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(form);
    }

}
