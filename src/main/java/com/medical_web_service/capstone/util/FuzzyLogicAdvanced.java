package com.medical_web_service.capstone.util;

public class FuzzyLogicAdvanced {

    public static double calculateSeverityScore(int dangerCount, int keywordCount, boolean urgency, boolean multiSymptom) {

        double score = 0;

        // 병원 즉시 권고 High
        if (urgency) score += 0.5;

        // 위험 신호 
        score += dangerCount * 0.2;

        // 키워드 → 부증상 분석
        score += keywordCount * 0.05;

        // 2개 이상 증상 조합 → 중간~높음 영향
        if (multiSymptom) score += 0.15;

        return Math.min(score, 1.0);
    }

    public static String level(double score) {
        if (score >= 0.7) return "높음";
        if (score >= 0.4) return "보통";
        return "낮음";
    }
}
