package com.medical_web_service.capstone.service;

import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.medical_web_service.capstone.entity.ApplicationStatus;
import com.medical_web_service.capstone.entity.DoctorApplication;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.DoctorApplicationRepository;
import com.medical_web_service.capstone.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorApplicationService {

    private final DoctorApplicationRepository doctorApplicationRepository;
    private final UserRepository userRepository;

    // 신청 생성
    public DoctorApplication apply(Long userId, String license, String hospitalName,MultipartFile file) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 중복 신청 방지
        if (doctorApplicationRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 신청한 상태입니다.");
        }
        // 파일 저장
        String filePath = saveFile(file);
        DoctorApplication app = new DoctorApplication();
        app.setUser(user);
        app.setStatus(ApplicationStatus.PENDING);
        app.setLicenseNumber(license);
        app.setHospitalName(hospitalName);
        app.setImagePath(filePath);
        return doctorApplicationRepository.save(app);
    }

    // 상태 조회
    public DoctorApplication getStatus(Long userId) {
        return doctorApplicationRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("신청 기록이 없습니다."));
    }
    private String saveFile(MultipartFile file) {
        try {
            String folder = "uploads/licenses/";
            File dir = new File(folder);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File saveFile = new File(folder + fileName);

            file.transferTo(saveFile);

            return folder + fileName;
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }
    }
}
