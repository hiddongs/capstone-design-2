package com.medical_web_service.capstone.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalDto {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String department;
    private double x;
    private double y;
    
}
