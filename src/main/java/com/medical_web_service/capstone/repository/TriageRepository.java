package com.medical_web_service.capstone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medical_web_service.capstone.entity.TriageForm;

@Repository
public interface TriageRepository extends JpaRepository<TriageForm, Long> {

    // ⭐ 사용자별 문진 기록 조회
    List<TriageForm> findByUserId(Long userId);

    // ⭐ 특정 진료과 문진 기록 조회 (선택)
    List<TriageForm> findByDepartment(String department);
}
