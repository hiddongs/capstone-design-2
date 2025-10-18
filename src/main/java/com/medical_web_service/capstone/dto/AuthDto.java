package com.medical_web_service.capstone.dto;

import java.util.Date;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginDto {
        private String username;
        private String password;

        @Builder
        public LoginDto(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignupDto {
        private String username;
        private String password;
        private String name;
        private String phone;
        @Temporal(TemporalType.DATE)
        private Date birthDate;

        private String gender;


        @Builder
        public SignupDto(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public static SignupDto encodePassword(SignupDto signupDto, String encodedPassword) {
            SignupDto newSignupDto = new SignupDto();
            newSignupDto.username = signupDto.getUsername();
            newSignupDto.password = encodedPassword;
            newSignupDto.name = signupDto.getName();
            newSignupDto.phone = signupDto.getPhone();
            newSignupDto.birthDate = signupDto.getBirthDate();
            newSignupDto.gender = signupDto.getGender();

            return newSignupDto;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;

        public TokenDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDto{
        private String username;
        private String password;
        private String name;
        private String newPassword; // 새로운 비밀번호

        private String phone;


        @Builder
        public UpdateDto(String username, String password,String newpassword, String name, String phone) {
            this.username = username;
            this.password = password;
            this.newPassword = newpassword; // 새로운 비밀번호
            this.name = name;
            this.phone = phone;}
    }
}