package com.medical_web_service.capstone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDto {
    private Long userId;
    private Long doctorId;
    private Long triageId;
    private String reservedTime;
    private String department;
}
