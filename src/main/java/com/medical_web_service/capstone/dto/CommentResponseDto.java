package com.medical_web_service.capstone.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private Long doctorId;
    private String comment;
    private String writer;
    private LocalDateTime createdTime;
}