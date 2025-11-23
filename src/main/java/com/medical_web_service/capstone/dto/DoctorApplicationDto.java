package com.medical_web_service.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoctorApplicationDto {
    private Long id;
    private Long userId;
    private String username;
    private String name;       // 사용자 이름
    private String licenseNumber;
    private String hospitalName;
    private String department;
    private String status;
}
