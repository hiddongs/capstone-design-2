package com.medical_web_service.capstone.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TriageRequestDto {
    private Long userId;
    private String department;
    private String departmentName;

    private List<QAItem> answers;

    @Getter
    @Setter
    public static class QAItem {
        private String question;
        private String answer;
    }
}
