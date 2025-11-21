package com.medical_web_service.capstone.dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.medical_web_service.capstone.entity.Role;
import com.medical_web_service.capstone.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String name;
    private Date birthDate;
    private String gender;
    private String phone;
    private Role role;
    private Integer career;
    private String department;
    private List<DiseaseHistoryDto> diseaseHistories;
    private List<SearchingDiseaseHistoryDto> searchingDiseaseHistories;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.birthDate = user.getBirthDate();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.career = user.getCareer();
        this.department = user.getDepartment();

        if (user.getDiseaseHistory() != null) {
            this.diseaseHistories = user.getDiseaseHistory()
                    .stream()
                    .map(DiseaseHistoryDto::new)
                    .collect(Collectors.toList());
        }

        if (user.getSearchingDiseaseHistories() != null) {
            this.searchingDiseaseHistories = user.getSearchingDiseaseHistories()
                    .stream()
                    .map(SearchingDiseaseHistoryDto::new)
                    .collect(Collectors.toList());
        }
    }
}
