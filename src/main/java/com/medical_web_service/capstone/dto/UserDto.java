package com.medical_web_service.capstone.dto;

import java.util.Date;

import com.medical_web_service.capstone.entity.Role;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String picture;
    @Temporal(TemporalType.DATE)
    private Date birthDate;  // 생년월일

    private String gender;
    private String diseaseHistory;
    private String searchingDiseaseHistory;
    private Role role;

    // 생성자

    public UserDto(Long id, String name, String nickname, String email, String picture, Date birthDate, String gender, String diseaseHistory, String searchingDiseaseHistory, Role role) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
        this.birthDate = birthDate;
        this.gender = gender;
        this.diseaseHistory = diseaseHistory;
        this.searchingDiseaseHistory = searchingDiseaseHistory;
        this.role = role;
    }
}
