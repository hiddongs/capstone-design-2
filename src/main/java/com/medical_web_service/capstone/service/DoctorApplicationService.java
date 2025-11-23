package com.medical_web_service.capstone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medical_web_service.capstone.dto.DoctorApplicationDto;
import com.medical_web_service.capstone.entity.ApplicationStatus;
import com.medical_web_service.capstone.entity.DoctorApplication;
import com.medical_web_service.capstone.entity.Role;
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
    public DoctorApplication apply(Long userId, String licenseNumber,
                                   String hospitalName, String department) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        DoctorApplication app = new DoctorApplication();
        app.setUser(user);
        app.setLicenseNumber(licenseNumber);
        app.setHospitalName(hospitalName);
        app.setDepartment(department);
        app.setStatus(ApplicationStatus.PENDING);

        return doctorApplicationRepository.save(app);
    }

    // 상태 조회
    public DoctorApplication getStatus(Long userId) {
        return doctorApplicationRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("신청 기록이 없습니다."));
    }

    // 전체 목록 조회
    public List<DoctorApplication> getAllApplications() {
        return doctorApplicationRepository.findAll();
    }
    public List<DoctorApplicationDto> getAllApplicationsDto() {
        return doctorApplicationRepository.findAllWithUser().stream()
                .map(app -> new DoctorApplicationDto(
                        app.getId(),
                        app.getUser().getId(),
                        app.getUser().getUsername(),
                        app.getUser().getName(),
                        app.getLicenseNumber(),
                        app.getHospitalName(),
                        app.getDepartment(),
                        app.getStatus().name()
                ))
                .toList();
    }


    // 승인 처리 
    @Transactional
    public void approve(Long appId) {
        DoctorApplication app = doctorApplicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        app.setStatus(ApplicationStatus.APPROVED);

        User user = app.getUser();
        user.setRole(Role.DOCTOR);
        user.setDepartment(app.getDepartment());  // OK

        doctorApplicationRepository.save(app);
        userRepository.save(user);
    }


    // 거절 처리 (상태만 변경해도 세션 유지됨)
    @Transactional
    public void reject(Long appId) {
        DoctorApplication app = doctorApplicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        app.setStatus(ApplicationStatus.REJECTED);
    }
}
