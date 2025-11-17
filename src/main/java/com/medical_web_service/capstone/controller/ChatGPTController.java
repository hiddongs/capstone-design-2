package com.medical_web_service.capstone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medical_web_service.capstone.dto.ChatCompletionDto;
import com.medical_web_service.capstone.dto.CompletionDto;
import com.medical_web_service.capstone.service.ChatGPTService;

import lombok.extern.slf4j.Slf4j;

/**
 * ChatGPT API
 *
 * @author : lee
 * @fileName : ChatGPTController
 * @since : 12/29/23
 */
@Profile("!export-schema")
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api/v1/chatGpt")
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    /**
     * [API] ChatGPT 모델 리스트를 조회합니다.
     */
    @GetMapping("/modelList")
    public ResponseEntity<List<Map<String, Object>>> selectModelList() {
        List<Map<String, Object>> result = chatGPTService.modelList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * [API] ChatGPT 유효한 모델인지 조회합니다.
     *
     * @param modelName
     * @return
     */
    @GetMapping("/model")
    public ResponseEntity<Map<String, Object>> isValidModel(@RequestParam(name = "modelName") String modelName) {
        Map<String, Object> result = chatGPTService.isValidModel(modelName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * [API] Legacy ChatGPT 프롬프트 명령을 수행합니다. : gpt-3.5-turbo-instruct, babbage-002, davinci-002
     *
     * @param completionDto {}
     * @return ResponseEntity<Map < String, Object>>
     */
    @PostMapping("/legacyPrompt")
    public ResponseEntity<Map<String, Object>> selectLegacyPrompt(@RequestBody CompletionDto completionDto) {
        log.debug("param :: " + completionDto.toString());
        Map<String, Object> result = chatGPTService.legacyPrompt(completionDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * [API] 최신 ChatGPT 프롬프트 명령어를 수행합니다. : gpt-4, gpt-4 turbo, gpt-3.5-turbo
     *
     * @param chatCompletionDto
     * @return
     */
    @PostMapping("/prompt")
    public ResponseEntity<Map<String, Object>> selectPrompt(@RequestBody ChatCompletionDto chatCompletionDto) {
        log.debug("param :: " + chatCompletionDto.toString());
        Map<String, Object> result = chatGPTService.prompt(chatCompletionDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * [API] 사용자가 제공한 증상에 맞는 질병을 추천합니다.
     *
     * @param symptomDescription 증상 설명
     * @return
     */
    @PostMapping("/diseases")
    public ResponseEntity<Map<String, Object>> recommendDiseases(@RequestParam(name = "symptomDescription") String symptomDescription) {
        log.debug("param :: " + symptomDescription);
        Map<String, Object> result = chatGPTService.recommendDiseases(symptomDescription);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    /**
     * 사용자 ID를 기반으로 건강 추천 정보를 반환
     * @param userId 사용자 ID
     * @return 건강 추천 결과
     */
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<Map<String, Object>> getHealthRecommendations(@PathVariable Long userId) {
        try {
            log.debug("사용자 ID로 건강 추천을 요청합니다: {}", userId);

            // 서비스 호출하여 결과 생성
            Map<String, Object> recommendations = chatGPTService.generateHealthRecommendations(userId);

            return ResponseEntity.ok(recommendations);
        } catch (IllegalArgumentException ex) {
            log.error("유효하지 않은 사용자 요청: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("서버 오류 발생: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "서버 오류가 발생했습니다. 다시 시도해 주세요."));
        }
    }
}