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
            String encodedPassword = encoder.encode(signupDto.getPassword());
            AuthDto.SignupDto newSignupDto = AuthDto.SignupDto.encodePassword(signupDto, encodedPassword);
            userService.registerUser(newSignupDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // 로그인 -> 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDto.LoginDto loginDto) {
        AuthDto.TokenDto tokenDto = authService.login(loginDto);

        User user = userService.findUserByUsername(loginDto.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Long userId = user.getId();
        String role = user.getRole().getKey();

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
                        "role", role,
                        "username", user.getUsername(),
                        "name", user.getName()
                ));
    }

    // 사용자 정보 업데이트
    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable(name = "userId") Long userId,
            @RequestBody AuthDto.UpdateDto updateDto) {

        try {
            String encodedPassword = encoder.encode(updateDto.getNewPassword());
            userService.updateUser(userId, updateDto, encodedPassword);
            return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 사용자 삭제
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Access Token 검증
    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader(name = "Authorization") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Refresh Token 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(name = "refresh-token") String requestRefreshToken,
            @RequestHeader(name = "Authorization") String requestAccessToken) {

        AuthDto.TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenDto != null) {
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .build();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken())
                    .build();
        } else {
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
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization") String requestAccessToken) {
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

    // 마이페이지 조회
    @GetMapping("/mypage/{userId}")
    public ResponseEntity<?> loadMyPage(
            @PathVariable(name = "userId") Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long loggedInUserId = userService.getLoggedInUserId(userDetails);

        if (!userId.equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.getUserWithHistories(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserDto dto = new UserDto(user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}/isDoctor")
    public boolean isDoctor(@PathVariable(name = "userId") Long userId) {
        return userService.userIsDoctor(userId);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        User user = userService.findUserById(userDetails.getUser().getId());

        return ResponseEntity.ok(Map.of(
                Map.entry("id", user.getId()),
                Map.entry("username", user.getUsername()),
                Map.entry("name", user.getName() == null ? "" : user.getName()),
                Map.entry("role", user.getRole().getKey()),
                Map.entry("department", user.getDepartment() == null ? "" : user.getDepartment()),
                Map.entry("career", user.getCareer() == null ? "" : user.getCareer())
        ));
    }
}
