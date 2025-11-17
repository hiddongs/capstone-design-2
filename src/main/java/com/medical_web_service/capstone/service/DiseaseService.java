package com.medical_web_service.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.medical_web_service.capstone.dto.DiseaseInfo;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DiseaseService {

    private static final Logger log = LoggerFactory.getLogger(DiseaseService.class);

    // classpath 상대경로만 보관
    private static final String ALL_DISEASE_DETAILS_PATH = "static/csv/all_disease_details.csv";
    private static final String DISEASE_SYMPTOMS_PATH    = "static/csv/disease_symptoms.csv";

    private final Map<String, DiseaseInfo> diseaseInfoMap;

    public DiseaseService() {
        diseaseInfoMap = new HashMap<>();
        loadDiseaseData();
        loadDiseaseSymptoms();
    }

    /** 질병 상세 정보를 로드 */
    private void loadDiseaseData() {
        var resource = new ClassPathResource(ALL_DISEASE_DETAILS_PATH);
        if (!resource.exists()) {
            log.error("리소스가 classpath에 없습니다: {}", ALL_DISEASE_DETAILS_PATH);
            return;
        }

        try (var isr = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             var reader = new CSVReader(isr)) {

            String[] line;

            // (선택) 헤더가 있으면 한 줄 스킵
            // reader.readNext();

            while ((line = reader.readNext()) != null) {
                if (line.length < 2) continue;

                String diseaseName = line[0];
                String detail      = line[1];

                DiseaseInfo diseaseInfo = diseaseInfoMap.getOrDefault(diseaseName, new DiseaseInfo());
                diseaseInfo.setName(diseaseName);

                List<String> details = diseaseInfo.getDetails() == null
                        ? new ArrayList<>()
                        : diseaseInfo.getDetails();
                details.add(detail);
                diseaseInfo.setDetails(details);

                diseaseInfoMap.put(diseaseName, diseaseInfo);
            }
        } catch (Exception e) {
            log.error("질병 상세 CSV 로드 실패", e);
        }
    }

    /** 질병 증상 데이터를 로드 */
    public void loadDiseaseSymptoms() {
        var resource = new ClassPathResource(DISEASE_SYMPTOMS_PATH);
        if (!resource.exists()) {
            log.error("리소스가 classpath에 없습니다: {}", DISEASE_SYMPTOMS_PATH);
            return;
        }

        try (var isr = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             var reader = new CSVReader(isr)) {

            String[] line;

            // (선택) 헤더가 있으면 한 줄 스킵
            // reader.readNext();

            while ((line = reader.readNext()) != null) {
                if (line.length < 3) continue;

                String diseaseName = line[0]; // 질병명
                String category    = line[1]; // 증상 유형
                String content     = line[2]; // 내용

                DiseaseInfo diseaseInfo = diseaseInfoMap.getOrDefault(diseaseName, new DiseaseInfo());
                diseaseInfo.setName(diseaseName);

                Map<String, List<String>> symptomsByCategory = diseaseInfo.getSymptomsByCategory();
                List<String> categoryContents = symptomsByCategory
                        .getOrDefault(category, new ArrayList<>());
                categoryContents.add(content);

                symptomsByCategory.put(category, categoryContents);
                diseaseInfo.setSymptomsByCategory(symptomsByCategory);

                diseaseInfoMap.put(diseaseName, diseaseInfo);
            }
        } catch (Exception e) {
            log.error("증상 CSV 로드 실패", e);
        }
    }

    /** 질병명을 입력받아 DiseaseInfo 반환 */
    public DiseaseInfo getDiseaseInfo(String diseaseName) {
        return diseaseInfoMap.getOrDefault(diseaseName, null);
    }

    /** DiseaseInfo를 포맷된 JSON 문자열로 반환 (이스케이프 제거) */
    public String getDiseaseInfoAsFormattedJson(String diseaseName) {
        DiseaseInfo diseaseInfo = getDiseaseInfo(diseaseName);
        if (diseaseInfo == null) return "{}";

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String jsonString = mapper.writeValueAsString(diseaseInfo);
            return StringEscapeUtils.unescapeJson(jsonString);
        } catch (JsonProcessingException e) {
            log.error("JSON 생성 실패", e);
            return "{}";
        }
    }

    /** 전체 질병 리스트 반환 */
    public List<DiseaseInfo> getAllDiseases() {
        return new ArrayList<>(diseaseInfoMap.values());
    }
}
