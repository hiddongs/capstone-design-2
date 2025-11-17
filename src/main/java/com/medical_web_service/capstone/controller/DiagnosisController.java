package com.medical_web_service.capstone.controller;

import com.medical_web_service.capstone.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/diagnosis")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping("/complete")
    public Map<String, Object> analyze(@RequestParam Long userId,
                                       @RequestParam String symptom) {
        return diagnosisService.analyzeSymptom(userId, symptom);
    }
}
