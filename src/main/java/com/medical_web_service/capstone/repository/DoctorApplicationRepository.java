package com.medical_web_service.capstone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medical_web_service.capstone.entity.DoctorApplication;

public interface DoctorApplicationRepository extends JpaRepository<DoctorApplication, Long> {
    Optional<DoctorApplication> findByUserId(Long userId);
}