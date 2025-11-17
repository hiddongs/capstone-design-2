package com.medical_web_service.capstone.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.medical_web_service.capstone.dto.ChatCompletionDto;
import com.medical_web_service.capstone.dto.ChatRequestMsgDto;
import com.medical_web_service.capstone.dto.TriageRequestDto;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class TriageAIService {

    private final ChatGPTService chatGPTService;

    public String generateSummary(TriageRequestDto dto) {

        String prompt = """
        당신은 현직 의사입니다.
        아래 문진 내용을 기반으로 환자의 상태를 3~5줄로 요약하세요.
        반드시 다음을 포함하세요:
        - 주요 증상
        - 발병 시기
        - 위험 요인
        - 현재 상태 평가
        - 진료 필요성

        문진 내용:
        """ + buildQAString(dto);

        // 📌 ChatCompletionDto 구성 (네 프로젝트 방식)
        ChatCompletionDto chatDto = new ChatCompletionDto();
        chatDto.setModel("gpt-3.5-turbo");

        ChatRequestMsgDto system = new ChatRequestMsgDto();
        system.setRole("system");
        system.setContent("당신은 전문 의사입니다. 문진 내용을 요약하는 AI 역할을 수행합니다.");

        ChatRequestMsgDto user = new ChatRequestMsgDto();
        user.setRole("user");
        user.setContent(prompt);

        chatDto.setMessages(List.of(system, user));

        // GPT 호출
        Map<String, Object> result = chatGPTService.prompt(chatDto);

        return extractMessage(result);
    }

    private String buildQAString(TriageRequestDto dto) {
        StringBuilder sb = new StringBuilder();
        dto.getAnswers().forEach(a ->
            sb.append(a.getQuestion())
              .append(": ")
              .append(a.getAnswer())
              .append("\n")
        );
        return sb.toString();
    }

    private String extractMessage(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> choice = choices.get(0);

            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            return "요약 생성 실패 (파싱 오류)";
        }
    }
}
