package com.medical_web_service.capstone.service;

import com.medical_web_service.capstone.dto.DiseaseHistoryDto;
import com.medical_web_service.capstone.entity.DiseaseHistory;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.DiseaseHistoryRepository;
import com.medical_web_service.capstone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDiseaseHistoryService {

    private final UserRepository userRepository;
    private final DiseaseHistoryRepository diseaseHistoryRepository;

    // 질병 이력 추가
    public DiseaseHistoryDto addDiseaseHistory(Long userId, DiseaseHistoryDto diseaseHistoryDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DiseaseHistory diseaseHistory = new DiseaseHistory(
                diseaseHistoryDTO.getDiseaseName(),
                diseaseHistoryDTO.getCompleteCureOrNot(),
                diseaseHistoryDTO.getDateOnOnset(),
                user
        );

        DiseaseHistory savedDiseaseHistory = diseaseHistoryRepository.save(diseaseHistory);
        return convertToDTO(savedDiseaseHistory);
    }

    // 특정 사용자의 질병 이력 조회
    public List<DiseaseHistoryDto> getDiseaseHistoriesByUserId(Long userId) {
        return diseaseHistoryRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 질병 이력 수정
    public DiseaseHistoryDto updateDiseaseHistory(Long userId, Long id, DiseaseHistoryDto updatedHistoryDTO) {
        DiseaseHistory existingHistory = diseaseHistoryRepository.findById(id)
                .filter(history -> history.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Disease history not found or access denied"));

        // 기존 객체의 필드 값을 업데이트
        existingHistory.update(
                updatedHistoryDTO.getDiseaseName(),
                updatedHistoryDTO.getCompleteCureOrNot(),
                updatedHistoryDTO.getDateOnOnset()
        );

        DiseaseHistory updatedHistory = diseaseHistoryRepository.save(existingHistory);
        return convertToDTO(updatedHistory);
    }

    // 질병 이력 삭제
    public void deleteDiseaseHistory(Long userId, Long id) {
        DiseaseHistory existingHistory = diseaseHistoryRepository.findById(id)
                .filter(history -> history.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Disease history not found or access denied"));

        diseaseHistoryRepository.delete(existingHistory);
    }
    // 모든 질병 이력 조회 (관리자용)
    public List<DiseaseHistoryDto> getAllDiseaseHistories() {
        return diseaseHistoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    private DiseaseHistoryDto convertToDTO(DiseaseHistory diseaseHistory) {
    	return new DiseaseHistoryDto(diseaseHistory);

        
    }
}
