package com.medical_web_service.capstone.repository;

import com.medical_web_service.capstone.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    List<Hospital> findByBusinessNameContaining(String keyword);
    List<Hospital> findByDepartmentContaining(String dept);
    List<Hospital> findByYBetweenAndXBetween(
            double minLat, double maxLat,
            double minLng, double maxLng
    );
}
