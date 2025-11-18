package com.medical_web_service.capstone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.service.DoctorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/get/{userId}/disease-history")
    public ResponseEntity<Map<String, Object>> getUserDiseaseHistory(@PathVariable Long userId) {
        // Service 로직 대신 Repository를 사용하여 데이터를 직접 가져옴

        Map<String, Object> userDisease = doctorService.getUserDiseaseHistoryWithInfo(userId);
        // HTTP 응답 반환
        return ResponseEntity.ok(userDisease);
    }
    
    
    @GetMapping("/list/{department}")
    public ResponseEntity<List<User>> getDoctors(@PathVariable String department) {
        List<User> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }

}
