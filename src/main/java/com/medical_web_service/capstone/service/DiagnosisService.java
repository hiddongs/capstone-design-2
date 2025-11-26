package com.medical_web_service.capstone.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical_web_service.capstone.dto.ChatCompletionDto;
import com.medical_web_service.capstone.dto.ChatRequestMsgDto;
import com.medical_web_service.capstone.entity.SearchingDiseaseHistory;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.SearchingDiseaseHistoryRepository;
import com.medical_web_service.capstone.repository.UserRepository;
import com.medical_web_service.capstone.util.FuzzyLogicAdvanced;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiagnosisService {

    private final ChatGPTService chatGPTService;
    private final UserRepository userRepository;
    private final SearchingDiseaseHistoryRepository historyRepository;

    public Map<String, Object> analyzeSymptom(Long userId, String symptom) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // GPT 프롬프트 구성
        ChatCompletionDto dto = new ChatCompletionDto();
        dto.setModel("gpt-4o-mini");

        String systemPrompt = """
            당신은 응급의학과 전문의를 보조하는 AI 의료 어시스턴트입니다.
            당신의 역할은 '진단'이 아니라 '선 분류(Pre-Triage)'입니다.
            다음 규칙을 반드시 지키세요:
            1. 확정 진단 금지 → 항상 "가능성이 있습니다" 형태 사용
            2. 위험 신호(마비, 언어장애, 발음장애, 의식저하)가 있으면 즉시 병원 방문 권고
            3. 출력은 반드시 아래 JSON 형식만 사용:

            {
              "suspectedDiseases": ["질환1","질환2"],
              "dangerSignals": ["어지러움","마비"],
              "recommendations": "사용자가 지금 취할 최소한의 안전 조치",
              "hospitalAdvice": "응급실 방문 필요 여부",
              "extractedKeywords": ["두통", "구토"]
            }
            """;

        ChatRequestMsgDto systemMsg = new ChatRequestMsgDto("system", systemPrompt);

        String userPrompt = "사용자의 증상: " + symptom +
                "\n위 JSON 형식에 맞게 결과를 만들어 주세요.";
        ChatRequestMsgDto userMsg = new ChatRequestMsgDto("user", userPrompt);

        dto.setMessages(List.of(systemMsg, userMsg));

        // GPT 호출
        Map<String, Object> gptRaw = chatGPTService.prompt(dto);

        // GPT JSON 파싱
        Map<String, Object> gpt = extractJsonFromGpt(gptRaw);

        // 안전 처리
        if (gpt.containsKey("error")) return gpt;

        // 데이터 추출
        List<String> dangerSignals = (List<String>) gpt.get("dangerSignals");
        List<String> keywords = (List<String>) gpt.get("extractedKeywords");

        boolean urgency = false;
        if (gpt.get("hospitalAdvice") != null) {
            urgency = gpt.get("hospitalAdvice").toString().contains("응급") ||
                      gpt.get("hospitalAdvice").toString().contains("즉시");
        }

        boolean multiSymptom = keywords.size() >= 2;

        // 퍼지 로직 계산
        double severityScore = FuzzyLogicAdvanced.calculateSeverityScore(
                dangerSignals.size(),
                keywords.size(),
                urgency,
                multiSymptom
        );

        String severityLevel = FuzzyLogicAdvanced.level(severityScore);

        // 최상위 질환명
        String diseaseName = "분류불가";
        List<String> diseases = (List<String>) gpt.get("suspectedDiseases");
        if (diseases != null && !diseases.isEmpty()) {
            diseaseName = diseases.get(0);
        }

        // DB 저장
        SearchingDiseaseHistory history = new SearchingDiseaseHistory();
        history.setUser(user);
        history.setSymptom(symptom);
        history.setAiResult(gpt.toString());
        history.setSeverityScore(severityScore);
        history.setSeverityLevel(severityLevel);
        history.setDiseaseName(diseaseName);

        historyRepository.save(history);

        // 프론트 반환
        Map<String, Object> result = new HashMap<>();
        result.put("ai", gpt);
        result.put("severityScore", severityScore);
        result.put("severityLevel", severityLevel);
        result.put("diseaseName", diseaseName);

        return result;
    }


    // GPT 메시지 → content JSON 추출
    private Map<String, Object> extractJsonFromGpt(Map<String, Object> raw) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) raw.get("choices");
            Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
            String content = msg.get("content").toString();
            log.info("🔥 GPT RAW JSON:\n" + content);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("GPT JSON 파싱 실패", e);
            return Map.of("error", "GPT JSON 파싱 실패");
        }
    }
}
