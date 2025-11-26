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

    // ✔ 의사별 예약 가능한 시간 조회
    @GetMapping("/slots/doctor/{doctorId}")
    public ResponseEntity<List<String>> getAvailableSlots(
            @PathVariable(name = "doctorId") Long doctorId
    ) {
        List<String> slots = reservationService.getSlotsByDoctor(doctorId);
        return ResponseEntity.ok(slots);
    }

    // ✔ 예약 생성
    @PostMapping("/create")
    public ResponseEntity<?> createReservation(
            @RequestBody ReservationDto dto
    ) {
        Long reservationId = reservationService.createReservation(dto);
        return ResponseEntity.ok(reservationId);
    }

    // ✔ 사용자 예약 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getUserReservations(
            @PathVariable(name = "userId") Long userId
    ) {
        List<Reservation> reservations = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    // ✔ 예약 상세 조회
    @GetMapping("/detail/{reservationId}")
    public ResponseEntity<Reservation> getReservationDetail(
            @PathVariable(name = "reservationId") Long reservationId
    ) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(reservation);
    }

    // ✔ 전체 예약 조회
    @GetMapping("/list")
    public ResponseEntity<List<Reservation>> getBookings() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
}
