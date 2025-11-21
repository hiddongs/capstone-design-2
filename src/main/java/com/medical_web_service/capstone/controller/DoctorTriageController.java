package com.medical_web_service.capstone.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medical_web_service.capstone.entity.TriageForm;
import com.medical_web_service.capstone.service.TriageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorTriageController {

    private final TriageService triageService;

    // ⭐ 의사별 문진 조회 API
    @GetMapping("/{doctorId}/triage")
    public ResponseEntity<List<TriageForm>> getDoctorTriageList(
            @PathVariable Long doctorId
    ) {
        List<TriageForm> list = triageService.getDoctorForms(doctorId);
        return ResponseEntity.ok(list);
    }
}
