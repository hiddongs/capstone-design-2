package com.medical_web_service.capstone.service;

import com.medical_web_service.capstone.entity.Role;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 사용자의 역할을 변경하는 메서드
     *
     * @param userId 변경할 사용자의 ID
     * @param newRole 새로운 역할 (Role Enum)
     * @return 변경된 사용자의 정보
     */
    public User changeUserRole(Long userId, Role newRole) {
        // 사용자 조회
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다.");
        }

        // 사용자 가져오기
        User user = userOptional.get();

        // 사용자 역할 변경
        user.setRole(newRole);
        return userRepository.save(user);
    }
    
}