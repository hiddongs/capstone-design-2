package com.medical_web_service.capstone.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical_web_service.capstone.dto.TriageRequestDto;
import com.medical_web_service.capstone.entity.TriageForm;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.TriageRepository;
import com.medical_web_service.capstone.repository.UserRepository;
import com.medical_web_service.capstone.util.FuzzyTriageEngine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TriageService {

    private final TriageRepository triageRepository;
    private final TriageAIService triageAIService;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public TriageForm submit(TriageRequestDto dto) throws Exception {

        //  GPT 요약 생성
        String aiSummary = triageAIService.generateSummary(dto);

        //  문진 응답에서 위험 관련 정보 추출
        int dangerCount = extractDangerCount(dto);
        int keywordCount = extractKeywordCount(dto);
        boolean urgency = detectUrgency(dto);
        boolean multiSymptom = keywordCount >= 2;

        //  사용자 나이 가져오기
        User user = userRepository.findById(dto.getUserId())
                .orElse(null);

        int age = (user != null && user.getAge() != null)
                ? user.getAge()
                : 30;   // 기본값


        //  퍼지 로직 위험도 계산
        double severityScore = FuzzyTriageEngine.calculateSeverity(
                dangerCount,
                keywordCount,
                urgency,
                multiSymptom,
                age
        );

        String severityLevel = FuzzyTriageEngine.level(severityScore);

        //  DB 저장
        TriageForm form = new TriageForm();
        form.setUserId(dto.getUserId());
        form.setDepartment(dto.getDepartment());
        form.setDepartmentName(dto.getDepartmentName());
        form.setDetailJson(mapper.writeValueAsString(dto.getAnswers()));
        form.setAiSummary(aiSummary);

        //  퍼지 결과 저장
        form.setSeverityScore(severityScore);
        form.setSeverityLevel(severityLevel);

        form.setCreatedAt(LocalDateTime.now());

        return triageRepository.save(form);
    }


    // --------------------------------------
    // 모든 답변(Object)을 문자열로 통일
    // --------------------------------------
    private String normalizeAnswer(Object ansObj) {
        if (ansObj == null) return "";

        // Array(List)일 경우 → "가슴,복부" 형태로 변환
        if (ansObj instanceof List) {
            List<?> list = (List<?>) ansObj;
            return String.join(",", list.stream().map(String::valueOf).toList());
        }

        // Number, Boolean 등 → 문자열로 변환
        return ansObj.toString();
    }


    // --------------------------------------
    // 위험 신호 감지 
    // --------------------------------------
    private int extractDangerCount(TriageRequestDto dto) {
        int count = 0;

        for (var a : dto.getAnswers()) {
            String ans = normalizeAnswer(a.getAnswer());

            if (ans.contains("호흡곤란")) count++;
            if (ans.contains("의식")) count++;
            if (ans.contains("마비")) count++;
            if (ans.contains("언어장애")) count++;
            if (ans.contains("발음 장애")) count++;
        }
        return count;
    }


    // --------------------------------------
    // 키워드 기반 증상 개수
    // --------------------------------------
    private int extractKeywordCount(TriageRequestDto dto) {
        int count = 0;

        for (var a : dto.getAnswers()) {
            Object ansObj = a.getAnswer();

            // multi-choice → ["가슴","복부"] → 2개
            if (ansObj instanceof List) {
                count += ((List<?>) ansObj).size();
            }
            // text/choice/scale → 1개
            else if (ansObj != null && !ansObj.toString().trim().isEmpty()) {
                count += 1;
            }
        }
        return count;
    }


    // --------------------------------------
    // 응급 여부 판단 
    // --------------------------------------
    private boolean detectUrgency(TriageRequestDto dto) {
        for (var a : dto.getAnswers()) {
            String ans = normalizeAnswer(a.getAnswer());

            if (ans.contains("심각") || ans.contains("즉시") || ans.contains("응급")) {
                return true;
            }
        }
        return false;
    }


    // --------------------------------------
    // 조회 메서드
    // --------------------------------------
    public List<TriageForm> getUserForms(Long userId) {
        return triageRepository.findByUserId(userId);
    }

    public List<TriageForm> getAllForms() {
        return triageRepository.findAll();
    }

    public TriageForm assignDoctor(Long triageId, Long doctorId) {
        TriageForm form = triageRepository.findById(triageId)
                .orElseThrow(() -> new RuntimeException("문진 정보 없음"));

        form.setDoctorId(doctorId);
        return triageRepository.save(form);
    }

    public TriageForm getById(Long id) {
        return triageRepository.findById(id).orElse(null);
    }
}
