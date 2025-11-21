package com.medical_web_service.capstone.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medical_web_service.capstone.dto.ReservationDto;
import com.medical_web_service.capstone.entity.Reservation;
import com.medical_web_service.capstone.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    // ✔ 의사별 예약 가능한 시간 조회 API
    @GetMapping("/slots/doctor/{doctorId}")
    public ResponseEntity<List<String>> getAvailableSlots(@PathVariable("doctorId") Long doctorId) {
        List<String> slots = reservationService.getSlotsByDoctor(doctorId);
        return ResponseEntity.ok(slots);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createReservation(@RequestBody ReservationDto dto) {
        Long reservationId = reservationService.createReservation(dto);
        return ResponseEntity.ok(reservationId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getUserReservations(@PathVariable("id") Long userId) {
        List<Reservation> reservations = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<Reservation>> getBookings() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
}
