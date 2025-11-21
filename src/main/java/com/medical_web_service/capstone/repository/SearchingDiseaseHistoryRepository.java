package com.medical_web_service.capstone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medical_web_service.capstone.entity.SearchingDiseaseHistory;
import com.medical_web_service.capstone.entity.User;

public interface SearchingDiseaseHistoryRepository extends JpaRepository<SearchingDiseaseHistory, Long> {
	
	  List<SearchingDiseaseHistory> findByUserId(Long userId);
	

}
