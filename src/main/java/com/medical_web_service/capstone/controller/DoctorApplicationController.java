package com.medical_web_service.capstone.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.medical_web_service.capstone.entity.DoctorApplication;
import com.medical_web_service.capstone.service.DoctorApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctor-apply")
@RequiredArgsConstructor
public class DoctorApplicationController {

    private final DoctorApplicationService doctorApplicationService;

    // 의사 전환 신청
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody Map<String, String> request) {

        Long userId = Long.valueOf(request.get("userId"));
        String license = request.get("licenseNumber");
        String hospital = request.get("hospitalName");

        DoctorApplication app = doctorApplicationService.apply(userId, license, hospital, null);

        return ResponseEntity.ok(Map.of(
                "message", "의사 전환 신청이 완료되었습니다.",
                "status", app.getStatus().name()
        ));
    }

    // 신청 상태 조회
    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getStatus(@PathVariable Long userId) {

        DoctorApplication app = doctorApplicationService.getStatus(userId);

        return ResponseEntity.ok(Map.of(
                "status", app.getStatus().name(),
                "licenseNumber", app.getLicenseNumber(),
                "hospitalName", app.getHospitalName()
        ));
    }
}
