package com.medical_web_service.capstone.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.medical_web_service.capstone.entity.DiseaseHistory;
import com.medical_web_service.capstone.entity.Reservation;
import com.medical_web_service.capstone.entity.Role;
import com.medical_web_service.capstone.entity.SearchingDiseaseHistory;
import com.medical_web_service.capstone.entity.TriageForm;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.DiseaseHistoryRepository;
import com.medical_web_service.capstone.repository.ReservationRepository;
import com.medical_web_service.capstone.repository.SearchingDiseaseHistoryRepository;

import com.medical_web_service.capstone.repository.TriageRepository;
import com.medical_web_service.capstone.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final UserRepository userRepository;
    private final UserService userService;
  
    private final TriageRepository triageRepository;
    private final DiseaseHistoryRepository diseaseHistoryRepository;
    private final SearchingDiseaseHistoryRepository searchingHistoryRepository;
    private final ReservationRepository reservationRepository;

    public Map<String, Object> getUserDiseaseHistoryWithInfo(Long userId) {
        // 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));        // 반환할 데이터 생성
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", user.getName()); // 유저 이름 또는 닉네임 추가

        if (user.getDiseaseHistory() != null && !user.getDiseaseHistory().isEmpty()) {
            List<String> diseaseHistoryList = user.getDiseaseHistory().stream()
                    .map(diseaseHistory -> diseaseHistory.getDiseaseName()) // 질병 이름만 가져오기
                    .collect(Collectors.toList());
            result.put("diseaseHistory", diseaseHistoryList);
        } else {
            result.put("diseaseHistory", Collections.emptyList()); // 질병 이력이 없는 경우 빈 리스트
        }

        return result;
    }

    public List<User> getDoctorsByDepartment(String department) {
        return userRepository.findByRoleAndDepartment(Role.DOCTOR, department);
    }
    public User getDoctorById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }
    
    public Map<String, Object> getPatientDetail(Long doctorId, Long userId) {

        Map<String, Object> result = new HashMap<>();

        // 1. 사용자 정보
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음: " + userId));
        result.put("user", user);

        // 2. 🔥 특정 의사에게 배정된 triage만 조회
        List<TriageForm> triageList =
                triageRepository.findByUserIdAndDoctorId(userId, doctorId);
        result.put("triageList", triageList);

        // 3. 질병 이력
        List<DiseaseHistory> diseaseList = diseaseHistoryRepository.findByUserId(userId);
        result.put("diseaseHistory", diseaseList);

        // 4. 검색 이력
        List<SearchingDiseaseHistory> searchHistory =
                searchingHistoryRepository.findByUserId(userId);
        result.put("searchHistory", searchHistory);

        // 5. 예약 이력
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        result.put("reservations", reservations);

        return result;
    }
    
    public List<Reservation> getDoctorReservations(Long doctorId) {
        return reservationRepository.findByDoctorId(doctorId);
    }


}
