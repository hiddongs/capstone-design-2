package com.medical_web_service.capstone.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.medical_web_service.capstone.dto.ReservationDto;
import com.medical_web_service.capstone.entity.Reservation;
import com.medical_web_service.capstone.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	
	private final ReservationRepository reservationRepository;
	
    public List<String> getSlotsByDoctor(Long doctorId) {

        List<String> slots = new ArrayList<>();

        
        
        // 09:00 ~ 17:00, 30분 간격
        LocalDateTime start = LocalDateTime.now()
                .withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now()
                .withHour(17).withMinute(0).withSecond(0).withNano(0);

        while (!start.isAfter(end)) {
            slots.add(start.toString());
            start = start.plusMinutes(30);
        }

        return slots;
    }
    
    public Long createReservation(ReservationDto dto) {
        Reservation r = new Reservation();
        r.setUserId(dto.getUserId());
        r.setDoctorId(dto.getDoctorId());
        r.setReservedTime(dto.getReservedTime());
        r.setDepartment(dto.getDepartment());
        r.setCreatedAt(new Date());
        r.setTriageId(dto.getTriageId());

        Reservation saved = reservationRepository.save(r);
        return saved.getId();
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
    }

    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId);
    }
}
