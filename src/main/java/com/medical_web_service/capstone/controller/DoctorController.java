package com.medical_web_service.capstone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Object>> getUserDiseaseHistory(
            @PathVariable(name = "userId") Long userId
    ) {
        Map<String, Object> userDisease = doctorService.getUserDiseaseHistoryWithInfo(userId);
        return ResponseEntity.ok(userDisease);
    }

    @GetMapping("/list/{department}")
    public ResponseEntity<List<User>> getDoctors(
            @PathVariable(name = "department") String department
    ) {
        List<User> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/by-dept/{department}")
    public ResponseEntity<List<User>> getDoctorsByDept(
            @PathVariable(name = "department") String department
    ) {
        System.out.println("요청받은 department = " + department);
        List<User> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<User> getDoctor(
            @PathVariable(name = "doctorId") Long doctorId
    ) {
        User doctor = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/{doctorId}/patient/{userId}")
    public ResponseEntity<?> getPatientDetail(
            @PathVariable(name = "doctorId") Long doctorId,
            @PathVariable(name = "userId") Long userId
    ) {
        Map<String, Object> result = doctorService.getPatientDetail(doctorId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{doctorId}/reservations")
    public ResponseEntity<List<Reservation>> getDoctorReservations(
            @PathVariable(name = "doctorId") Long doctorId
    ) {
        return ResponseEntity.ok(doctorService.getDoctorReservations(doctorId));
    }

    @GetMapping("/{doctorId}/unanswered-boards")
    public ResponseEntity<?> getUnansweredBoards(
            @PathVariable(name = "doctorId") Long doctorId
    ) {
        List<?> boards = doctorService.getUnansweredBoards(doctorId);
        return ResponseEntity.ok(boards);
    }
}
