package com.medical_web_service.capstone.service;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class DiseaseNameComparator implements Comparator<String> {

    private final Collator koreanCollator;

    public DiseaseNameComparator() {
        koreanCollator = Collator.getInstance(Locale.KOREAN);
        koreanCollator.setStrength(Collator.PRIMARY);
    }

    @Override
    public int compare(String name1, String name2) {
        // 숫자 우선 정렬
        if (isNumeric(name1) && isNumeric(name2)) {
            return Integer.compare(Integer.parseInt(name1), Integer.parseInt(name2));
        } else if (isNumeric(name1)) {
            return -1;  // name1이 숫자면 먼저 옴
        } else if (isNumeric(name2)) {
            return 1;   // name2가 숫자면 name1 뒤로 옴
        }

        // 영어 우선 정렬
        if (isAlphabet(name1) && isAlphabet(name2)) {
            return name1.compareToIgnoreCase(name2);
        } else if (isAlphabet(name1)) {
            return -1;  // name1이 영어면 먼저 옴
        } else if (isAlphabet(name2)) {
            return 1;   // name2가 영어면 name1 뒤로 옴
        }

        // 한글 자모 순서 정렬
        return koreanCollator.compare(name1, name2);
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private boolean isAlphabet(String str) {
        return str.matches("^[a-zA-Z]+$");
    }
}

