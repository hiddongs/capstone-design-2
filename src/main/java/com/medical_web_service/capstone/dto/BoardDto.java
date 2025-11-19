package com.medical_web_service.capstone.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BoardDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateBoardDto{
        private String title;
        private String content;
        private String symptom;      // 추가
        private String department;   // 추가
        private boolean anonymous;   // 추가


    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateBoardDto{
        private String title;
        private String content;
        private String writer;
        private String symptom;
        private String department;
        private boolean anonymous;

    }

    @Getter
    @NoArgsConstructor
    public static class PostDetailsDTO {
        private Long id;
        private String title;
        private String content;
        private String writer;
        private Long userId; // 작성자의 userId 필드 추가
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private String symptom;
        private String department;
        private boolean anonymous;

        public PostDetailsDTO(
                Long id,
                String title,
                String content,
                String writer,
                Long userId,
                LocalDateTime createdDate,
                LocalDateTime modifiedDate,
                String symptom,
                String department,
                boolean anonymous
        ) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.writer = writer;
            this.userId = userId;
            this.createdDate = createdDate;
            this.modifiedDate = modifiedDate;
            this.symptom = symptom;
            this.department = department;
            this.anonymous = anonymous;
        }


    }
}
