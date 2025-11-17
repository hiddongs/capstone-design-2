package com.medical_web_service.capstone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medical_web_service.capstone.entity.Hospital;
import com.medical_web_service.capstone.service.HospitalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping("/search")
    public List<Hospital> searchByKeyword(@RequestParam String query) {
        return hospitalService.searchByKeyword(query);
    }

    @GetMapping("/department")
    public List<Hospital> searchByDepartment(@RequestParam("dept") String dept) {
        return hospitalService.searchByDepartment(dept);
    }
    @GetMapping("/nearby")
    public List<Map<String, String>> getNearbyHospitals(
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        return hospitalService.searchNearby(lat, lng);
    }
}
