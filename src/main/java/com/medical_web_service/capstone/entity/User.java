package com.medical_web_service.capstone.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.medical_web_service.capstone.dto.AuthDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;    // Principal

    @Column(nullable = false)
    private String password;    // Credential

    private String name;
    private Date birthDate;
    private String gender;

    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 게시판 목록
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Board> boards = new HashSet<>();

    // 유저 질병 이력
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<DiseaseHistory> diseaseHistory = new HashSet<>();

    // GPT 검색 이력
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SearchingDiseaseHistory> searchingDiseaseHistories = new HashSet<>();

    // 이미지
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Image> images = new HashSet<>();
    private Integer career; // 경력 (년수)

    private String department; // 진료과 (internal / ent / pediatrics ...)

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

    // 게시글 연결
    public void addBoard(Board board) {
        this.boards.add(board);
        board.setUser(this);
    }
}
