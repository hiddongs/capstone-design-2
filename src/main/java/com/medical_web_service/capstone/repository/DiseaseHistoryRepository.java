package com.medical_web_service.capstone.repository;

import com.medical_web_service.capstone.entity.DiseaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseHistoryRepository extends JpaRepository<DiseaseHistory, Long> {
    List<DiseaseHistory> findByUserId(Long userId);
    
    
}
