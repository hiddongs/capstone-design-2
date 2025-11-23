package com.medical_web_service.capstone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.medical_web_service.capstone.entity.DoctorApplication;

public interface DoctorApplicationRepository extends JpaRepository<DoctorApplication, Long> {
    Optional<DoctorApplication> findByUserId(Long userId);
    
    @Query("SELECT da FROM DoctorApplication da JOIN FETCH da.user")
    List<DoctorApplication> findAllWithUser();

}