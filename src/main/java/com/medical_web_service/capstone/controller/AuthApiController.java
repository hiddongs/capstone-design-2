package com.medical_web_service.capstone.controller;

import java.util.Map;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medical_web_service.capstone.dto.AuthDto;
import com.medical_web_service.capstone.dto.UserDto;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.service.AuthService;
import com.medical_web_service.capstone.service.UserDetailsImpl;
import com.medical_web_service.capstone.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;
    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    private final long COOKIE_EXPIRATION = 7776000; // 90일

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid AuthDto.SignupDto signupDto) {
    	try {
        String encodedPassword = encoder.encode(signupDto.getPassword()); // 비밀번호 암호화
        AuthDto.SignupDto newSignupDto = AuthDto.SignupDto.encodePassword(signupDto, encodedPassword);

        userService.registerUser(newSignupDto);
        return new ResponseEntity<>(HttpStatus.OK);
    	}catch(IllegalArgumentException e){
    		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    	}
    }

 // 로그인 -> 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDto.LoginDto loginDto) {
        // 인증 처리
        AuthDto.TokenDto tokenDto = authService.login(loginDto);

        // 사용자 정보 가져오기
        User user = userService.findUserByUsername(loginDto.getUsername());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Long userId = user.getId();
        String role = user.getRole().getKey(); // ROLE_DOCTOR, ROLE_USER 등

        // Refresh Token 저장
        HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
                .body(Map.of(
                        "accessToken", tokenDto.getAccessToken(),
                        "userId", userId,
                        "role", role,              // ⭐ 추가됨
                        "username", user.getUsername(),
                        "name", user.getName()
                ));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody AuthDto.UpdateDto updateDto) {
        try {
            String encodedPassword = encoder.encode(updateDto.getNewPassword()); // 새로운 비밀번호 암호화
            userService.updateUser(userId, updateDto, encodedPassword);
            return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build(); // 재발급 필요X
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 재발급 필요
        }
    }
    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {
        AuthDto.TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenDto != null) { // 토큰 재발급 성공
            // RT 저장
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .build();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    // AT 저장
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken())
                    .build();

        } else { // Refresh Token 탈취 가능성
            // Cookie 삭제 후 재로그인 유도
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String requestAccessToken) {
        authService.logout(requestAccessToken);
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @GetMapping("/mypage/{userId}")
    public ResponseEntity<?> loadMyPage(@PathVariable("userId") Long userId, 
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 현재 로그인한 사용자 ID
        Long loggedInUserId = userService.getLoggedInUserId(userDetails);
        System.out.println("LoggedInUserId: " + loggedInUserId);
        System.out.println("RequestedUserId: " + userId);

        // 로그인한 사용자 = 요청한 사용자인지 검증
        if (!userId.equals(loggedInUserId)) {
            System.out.println("Unauthorized access attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // fetch join으로 diseaseHistory + searchingDiseaseHistories 모두 가져옴
        User user = userService.getUserWithHistories(userId);
        System.out.println("User: " + user);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // DTO 변환 — LazyInitializationException 없음
        UserDto dto = new UserDto(user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}/isDoctor")
    public boolean isDoctor(@PathVariable Long userId) {
        return userService.userIsDoctor(userId);
    }
}
