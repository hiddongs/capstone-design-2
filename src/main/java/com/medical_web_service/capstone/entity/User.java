package com.medical_web_service.capstone.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.medical_web_service.capstone.dto.AuthDto;
import com.medical_web_service.capstone.entity.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // Principal
    @Column(nullable = false)
    private String password; // Credential

    private String name;

    private Date birthDate;

    private String gender;
    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role; // 사용자 권한

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DiseaseHistory> diseaseHistory;

    @OneToMany(mappedBy = "user")
    private List<SearchingDiseaseHistory> searchingDiseaseHistories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    // == 생성 메서드 == //
    public static User registerUser(AuthDto.SignupDto signupDto) {
        User user = new User();

        user.username = signupDto.getUsername();
        user.password = signupDto.getPassword();
        user.name = signupDto.getName();
        user.birthDate = signupDto.getBirthDate();
        user.gender = signupDto.getGender();
        user.phone = signupDto.getPhone();
        user.role = Role.USER;

        return user;
    }

    public void addBoard(Board board) {
        this.boards.add(board);
        board.setUser(this); // 보드의 사용자 정보를 설정합니다.
    }
}
