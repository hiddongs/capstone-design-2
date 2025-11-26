
package com.medical_web_service.capstone.util;

public class FuzzyTriageEngine {

    // ------------------------------
    // 멤버십 함수
    // ------------------------------
    private static double dangerLow(int d) {
        if (d <= 1) return 1;
        if (d == 2) return 0.3;
        return 0;
    }

    private static double dangerMedium(int d) {
        if (d == 1) return 0.5;
        if (d == 2) return 1;
        if (d == 3) return 0.6;
        return 0;
    }

    private static double dangerHigh(int d) {
        if (d == 2) return 0.3;
        if (d >= 3) return 1;
        return 0;
    }

    private static double keywordLow(int k) {
        if (k <= 1) return 1;
        if (k <= 3) return 0.4;
        return 0;
    }

    private static double keywordMedium(int k) {
        if (k == 2) return 0.8;
        if (k == 3) return 1;
        if (k == 4) return 0.6;
        return 0;
    }

    private static double keywordHigh(int k) {
        return (k >= 4) ? 1.0 : 0.0;
    }

    private static double urgencyTrue(boolean u) {
        return u ? 1.0 : 0.0;
    }

    private static double urgencyFalse(boolean u) {
        return u ? 0.0 : 1.0;
    }

    private static double multiTrue(boolean m) {
        return m ? 1.0 : 0.0;
    }

    private static double multiFalse(boolean m) {
        return m ? 0.0 : 1.0;
    }

    private static double ageYoung(int age) {
        if (age <= 40) return 1;
        if (age <= 50) return 0.5;
        return 0;
    }

    private static double ageMiddle(int age) {
        return (age >= 30 && age <= 60) ? 1 : 0;
    }

    private static double ageOld(int age) {
        if (age >= 55) return 1;
        if (age >= 45) return 0.4;
        return 0;
    }

    // ------------------------------
    // 퍼지 규칙 기반 추론
    // ------------------------------
    public static double calculateSeverity(int danger, int keyword, boolean urgency, boolean multi, int age) {

        double high1 = Math.min(dangerHigh(danger), urgencyTrue(urgency));
        double high2 = Math.min(dangerMedium(danger), keywordHigh(keyword));
        double high3 = Math.min(ageOld(age), dangerMedium(danger));
        double high4 = Math.min(multiTrue(multi), keywordHigh(keyword));

        double severityHigh = Math.max(Math.max(high1, high2), Math.max(high3, high4));

        double med1 = Math.min(dangerLow(danger), keywordHigh(keyword));
        double med2 = Math.min(dangerMedium(danger), keywordMedium(keyword));
        double med3 = Math.min(multiTrue(multi), keywordMedium(keyword));

        double severityMedium = Math.max(Math.max(med1, med2), med3);

        double low1 = Math.min(dangerLow(danger), keywordLow(keyword));
        double low2 = Math.min(urgencyFalse(urgency), dangerLow(danger));

        double severityLow = Math.max(low1, low2);

        // ------------------------------
        //  비퍼지화 
        // ------------------------------
        double numerator =
                severityLow * 0.2 +
                severityMedium * 0.5 +
                severityHigh * 0.8;

        double denominator = severityLow + severityMedium + severityHigh;

        if (denominator == 0) return 0.0;

        return Math.min(numerator / denominator, 1.0);
    }

    // ------------------------------
    //  위험도 레벨
    // ------------------------------
    public static String level(double score) {
        if (score >= 0.8) return "응급";
        if (score >= 0.6) return "높음";
        if (score >= 0.4) return "보통";
        if (score >= 0.2) return "낮음";
        return "매우 낮음";
    }
}
