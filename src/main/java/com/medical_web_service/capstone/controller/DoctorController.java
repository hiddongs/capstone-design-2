package com.medical_web_service.capstone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medical_web_service.capstone.entity.Reservation;
import com.medical_web_service.capstone.entity.User;
import com.medical_web_service.capstone.repository.ReservationRepository;
import com.medical_web_service.capstone.service.DoctorService;
import com.medical_web_service.capstone.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final ReservationService reservationService;
    
    private final ReservationRepository reservationRepository;
    @GetMapping("/get/{userId}/disease-history")
    public ResponseEntity<Map<String, Object>> getUserDiseaseHistory(@PathVariable Long userId) {
        // Service 로직 대신 Repository를 사용하여 데이터를 직접 가져옴

        Map<String, Object> userDisease = doctorService.getUserDiseaseHistoryWithInfo(userId);
        // HTTP 응답 반환
        return ResponseEntity.ok(userDisease);
    }
    
    
    @GetMapping("/list/{department}")
    public ResponseEntity<List<User>> getDoctors(@PathVariable String department) {
        List<User> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }
   
    @GetMapping("/by-dept/{department}")
    public ResponseEntity<List<User>> getDoctorsByDept(@PathVariable("department") String department) {
        System.out.println("요청받은 department = " + department);
        List<User> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<User> getDoctor(@PathVariable Long doctorId) {
        User doctor = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/{doctorId}/patient/{userId}")
    public ResponseEntity<?> getPatientDetail(
            @PathVariable Long doctorId,
            @PathVariable Long userId
    ) {
        Map<String, Object> result = doctorService.getPatientDetail(doctorId, userId);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{doctorId}/reservations")
    public ResponseEntity<List<Reservation>> getDoctorReservations(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorReservations(doctorId));
    }



}
