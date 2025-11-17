package com.medical_web_service.capstone.util;

import java.util.Arrays;
import java.util.List;

public class FuzzyLogicUtil {

    private static final List<String> HIGH_RISK_KEYWORDS = Arrays.asList(
            "응급", "즉시", "마비", "뇌졸중", "출혈", "심각", "고열", "호흡곤란"
    );

    private static final List<String> MID_RISK_KEYWORDS = Arrays.asList(
            "어지러움", "구토", "두통", "복통", "발열", "통증"
    );

    public static double calculateSeverityScore(String aiText) {
        String lower = aiText.toLowerCase();
        double score = 0.0;

        for (String word : HIGH_RISK_KEYWORDS) {
            if (lower.contains(word)) score += 0.3;
        }

        for (String word : MID_RISK_KEYWORDS) {
            if (lower.contains(word)) score += 0.15;
        }

        return Math.min(score, 1.0); // 최대 1.0
    }

    public static String getSeverityLevel(double score) {
        if (score >= 0.7) return "높음";
        if (score >= 0.4) return "보통";
        return "낮음";
    }
}
