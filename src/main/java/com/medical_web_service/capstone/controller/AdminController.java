package com.medical_web_service.capstone.controller;


import com.medical_web_service.capstone.entity.Role;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 사용자의 역할을 변경하는 엔드포인트
     *
     * @param userId 변경할 사용자의 ID
     * @param newRole 새로운 역할 이름
     * @return 변경된 사용자 정보 또는 오류 메시지
     */
    @PutMapping("/change-role/{userId}")
    public ResponseEntity<?> changeUserRole(@PathVariable Long userId, @RequestParam String newRole) {
        try {
            // 새로운 역할 이름을 Enum으로 변환
            Role role = Role.valueOf(newRole.toUpperCase());

            // 사용자 역할 변경
            User updatedUser = adminService.changeUserRole(userId, role);

            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            // Enum 변환 실패 또는 사용자/역할 미존재 시 발생하는 예외 처리
            return ResponseEntity.badRequest().body("유효하지 않은 역할 이름이거나 사용자가 존재하지 않습니다.");
        } catch (Exception e) {
            // 그 외 예외 처리
            return ResponseEntity.status(500).body("역할 변경 중 오류가 발생했습니다.");
        }
    }

    
}