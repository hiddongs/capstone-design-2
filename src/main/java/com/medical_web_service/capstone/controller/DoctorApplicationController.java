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

    @PostMapping("/apply")
    public ResponseEntity<?> apply(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "licenseNumber") String licenseNumber,
            @RequestParam(name = "hospitalName") String hospitalName,
            @RequestParam(name = "department") String department
    ) {
        DoctorApplication app = doctorApplicationService.apply(
                userId, licenseNumber, hospitalName, department
        );

        return ResponseEntity.ok(Map.of(
                "message", "의사 전환 신청이 완료되었습니다.",
                "status", app.getStatus().name()
        ));
    }

    // 신청 상태 조회
    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getStatus(
            @PathVariable(name = "userId") Long userId
    ) {
        DoctorApplication app = doctorApplicationService.getStatus(userId);

        return ResponseEntity.ok(Map.of(
                "status", app.getStatus().name(),
                "licenseNumber", app.getLicenseNumber(),
                "hospitalName", app.getHospitalName()
        ));
    }

    // 관리자 승인
    @PostMapping("/approve/{appId}")
    public ResponseEntity<?> approve(
            @PathVariable(name = "appId") Long appId
    ) {
        doctorApplicationService.approve(appId);
        return ResponseEntity.ok(Map.of("message", "승인 완료"));
    }

    // 관리자 거절
    @PostMapping("/reject/{appId}")
    public ResponseEntity<?> reject(
            @PathVariable(name = "appId") Long appId
    ) {
        doctorApplicationService.reject(appId);
        return ResponseEntity.ok(Map.of("message", "거절 완료"));
    }

    // 전체 신청 목록 조회 (관리자 전용)
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(doctorApplicationService.getAllApplicationsDto());
    }
}
