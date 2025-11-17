package com.medical_web_service.capstone.service;


import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.medical_web_service.capstone.dto.ChatCompletionDto;
import com.medical_web_service.capstone.dto.CompletionDto;

/**
 * ChatGPT 서비스 인터페이스
 *
 * @author : lee
 * @fileName : ChatGPTService
 * @since : 12/29/23
 */
@Profile("!export-schema")
@Service
public interface ChatGPTService {

    List<Map<String, Object>> modelList();

    Map<String, Object> isValidModel(String modelName);

    Map<String, Object> legacyPrompt(CompletionDto completionDto);

    Map<String, Object> prompt(ChatCompletionDto chatCompletionDto);

    public Map<String, Object> recommendDiseases(String symptomDescription);

    public Map<String, Object> legacyDiseasePrompt(String symptomDescription);
    public Map<String, Object> generateHealthRecommendations(Long userId);

}