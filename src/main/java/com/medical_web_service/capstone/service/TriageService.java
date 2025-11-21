package com.medical_web_service.capstone.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical_web_service.capstone.dto.TriageRequestDto;
import com.medical_web_service.capstone.entity.TriageForm;
import com.medical_web_service.capstone.repository.TriageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TriageService {

    private final TriageRepository triageRepository;
    private final TriageAIService triageAIService;
    private final ObjectMapper mapper;

    public TriageForm submit(TriageRequestDto dto) throws Exception {

        String aiSummary = triageAIService.generateSummary(dto);

        TriageForm form = new TriageForm();
        form.setUserId(dto.getUserId());
        form.setDepartment(dto.getDepartment());
        form.setDepartmentName(dto.getDepartmentName());
        form.setDetailJson(mapper.writeValueAsString(dto.getAnswers()));
        form.setAiSummary(aiSummary);
        form.setCreatedAt(LocalDateTime.now());

        return triageRepository.save(form);
    }

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

    public List<TriageForm> getDoctorForms(Long doctorId) {
        return triageRepository.findByDoctorId(doctorId);
    }
    public TriageForm getById(Long id) {
        return triageRepository.findById(id).orElse(null);
    }

}

