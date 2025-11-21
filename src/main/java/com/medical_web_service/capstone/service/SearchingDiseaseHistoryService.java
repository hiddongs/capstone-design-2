package com.medical_web_service.capstone.service;

import com.medical_web_service.capstone.entity.SearchingDiseaseHistory;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.SearchingDiseaseHistoryRepository;
import com.medical_web_service.capstone.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchingDiseaseHistoryService {
    private final SearchingDiseaseHistoryRepository searchingDiseaseHistoryRepository;
    private final UserRepository userRepository;

    // 사용자의 질병 검색 기록 저장
    public SearchingDiseaseHistory storeDisease(Long userId, String diseaseName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 새로운 SearchingDiseaseHistory 엔티티 생성 및 저장
        SearchingDiseaseHistory history = new SearchingDiseaseHistory();
        history.setUser(user);
        history.setDiseaseName(diseaseName);

        return searchingDiseaseHistoryRepository.save(history);
    }

    // 사용자의 모든 질병 검색 기록 반환
    public List<SearchingDiseaseHistory> getHistoryByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return searchingDiseaseHistoryRepository.findByUserId(userId);
    }

    // 사용자의 특정 검색 기록 삭제
    public void deleteHistoryByIdAndUser(Long userId, Long historyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        SearchingDiseaseHistory history = searchingDiseaseHistoryRepository.findById(historyId)
                .orElseThrow(() -> new EntityNotFoundException("SearchingDiseaseHistory not found with id: " + historyId));

        // 사용자가 기록의 소유자인지 확인
        if (!history.getUser().equals(user)) {
            throw new IllegalArgumentException("The record does not belong to the specified user.");
        }

        searchingDiseaseHistoryRepository.delete(history);
    }
}
