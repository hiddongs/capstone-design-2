package com.medical_web_service.capstone.service;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical_web_service.capstone.entity.Hospital;
import com.medical_web_service.capstone.repository.HospitalRepository;
import com.opencsv.CSVReader;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private static final Logger log = LoggerFactory.getLogger(HospitalService.class);
    private static final String HOSPITAL_CSV_PATH = "static/csv/hospital.csv";

    private final HospitalRepository hospitalRepository;

    
 // EPSG5174 → WGS84 변환 함수
    private double[] convert5174ToWGS84(double x, double y) {
        // 중부원점 TM → WGS84
        double dx = x - 200000;
        double dy = y - 500000;

        double lat = dy * 0.000008983 + 38.0;
        double lng = dx * 0.000010966 + 127.0;

        return new double[]{lat, lng};
    }

    @PostConstruct
    public void loadCsv() {
        var resource = new ClassPathResource(HOSPITAL_CSV_PATH);

        if (!resource.exists()) {
            log.error("❌ 병원 CSV 리소스를 찾을 수 없습니다: {}", HOSPITAL_CSV_PATH);
            return;
        }

        int success = 0;
        int fail = 0;

        try (
            var isr = new InputStreamReader(resource.getInputStream(), Charset.forName("EUC-KR"));
            var reader = new CSVReader(isr)
        ) {
            String[] col;
            reader.readNext(); // 헤더 스킵

            while ((col = reader.readNext()) != null) {
                try {

                    // X/Y 좌표 변환 (EPSG5174 → WGS84)
                    Double x5174 = parseDoubleSafe(col[26]);
                    Double y5174 = parseDoubleSafe(col[27]);

                    Double realX = null;
                    Double realY = null;

                    if (x5174 != null && y5174 != null) {
                        double[] wgs = convert5174ToWGS84(x5174, y5174);
                        realY = wgs[0]; // 위도
                        realX = wgs[1]; // 경도
                    }

                    Hospital hospital = Hospital.builder()
                            .businessName(clean(col[21]))
                            .address(clean(col[18]))
                            .roadAddress(clean(col[19]))
                            .phone(clean(col[15]))

                            .department(clean(col[34]))       // 기존 사용
                            .departmentName(clean(col[34]))   // 상세 진료과

                            .type(clean(col[28]))            // 기존
                            .medicalType(clean(col[28]))     // 상세

                            .doctorCount(parseIntSafe(col[29]))
                            .roomCount(parseIntSafe(col[30]))
                            .bedCount(parseIntSafe(col[31]))

                            .status(clean(col[8]))
                            .statusDetail(clean(col[10]))   // 상세영업상태명 ← 추가!
                            .x(realX)
                            .y(realY)
                            .build();


                    hospitalRepository.save(hospital);
                    success++;

                } catch (Exception e) {
                    fail++;
                    log.warn("⚠️ 병원 파싱 실패: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("❌ 병원 CSV 로딩 실패", e);
        }

        log.info("✅ 병원 CSV 로딩 완료 - 성공: {}건 / 실패: {}건", success, fail);
    }

    private Integer parseIntSafe(String str) {
        try {
            if (str == null) return null;
            str = str.replace("\"", "").trim();
            return Integer.parseInt(str);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Hospital> searchByKeyword(String keyword) {
        return hospitalRepository.findByBusinessNameContaining(keyword);
    }

    public List<Hospital> searchByDepartment(String dept) {
        return hospitalRepository.findByDepartmentContaining(dept);
    }

    private String clean(String str) {
        if (str == null) return null;
        str = str.replace("\"", "").trim();
        return str.replaceAll("[^\\x00-\\x7F\\p{L}\\p{N}\\s·.,()\\-]", "");
    }

    private Double parseDoubleSafe(String str) {
        if (str == null) return null;
        str = str.replace("\"", "").trim();
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return null;
        }
    }
    private Map<String, Double> geocodeAddress(String address) {
        try {
            String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="
                    + URLEncoder.encode(address, StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-NCP-APIGW-API-KEY-ID", "27pxj9zl1c");
            headers.set("X-NCP-APIGW-API-KEY", "qTCCJkZCUYNoZsFt8QqY7MgUQvGdOMbcxzskkOqmt");

            HttpEntity<Void> request = new HttpEntity<>(headers);
            RestTemplate rest = new RestTemplate();

            ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode addresses = mapper.readTree(response.getBody()).get("addresses");

            if (addresses == null || addresses.size() == 0) return null;

            JsonNode addr = addresses.get(0);

            return Map.of(
                    "lng", addr.get("x").asDouble(),
                    "lat", addr.get("y").asDouble()
            );

        } catch (Exception e) {
            log.warn("⚠ 주소 지오코딩 실패: {}", e.getMessage());
            return null;
        }
    }
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371000; // 지구 반지름 (미터)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // 거리(m)
    }

    
    public List<Map<String, String>> searchNearby(double lat, double lng) {

        String query = "병원";
        String localSearchUrl = "https://openapi.naver.com/v1/search/local.json"
                + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&display=30"; // 많이 가져와야 필터링 가능

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", "27pxj9zl1c");
        headers.set("X-Naver-Client-Secret", "qTCCJkZCUYNoZsFt8QqY7MgUQvGdOMbcxzskkOqmt");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate rest = new RestTemplate();

        List<Map<String, String>> resultList = new ArrayList<>();

        try {
            // 1) 일반 검색으로 병원 데이터 가져오기
            ResponseEntity<String> response = rest.exchange(localSearchUrl, HttpMethod.GET, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(response.getBody()).get("items");

            if (items == null || !items.isArray()) return resultList;

            // 2) 각 병원 주소를 Geocoding API로 좌표 변환
            for (JsonNode item : items) {

                String name = clean(item.get("title").asText());
                String address = clean(item.get("roadAddress").asText());

                if (address == null || address.isBlank()) continue;

                // 주소 → 좌표 변환
                Map<String, Double> coords = geocodeAddress(address);
                if (coords == null) continue;

                double hospLat = coords.get("lat");
                double hospLng = coords.get("lng");

                // 3) 거리 계산
                double distance = calculateDistance(lat, lng, hospLat, hospLng);

                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("address", address);
                map.put("telephone", clean(item.get("telephone").asText()));
                map.put("distance", String.valueOf(distance));
                map.put("lat", String.valueOf(hospLat));
                map.put("lng", String.valueOf(hospLng));

                resultList.add(map);
            }

            // 4) 거리 가까운 순으로 정렬
            resultList.sort(Comparator.comparingDouble(
                    m -> Double.parseDouble(m.get("distance"))
            ));

            // 5) 상위 5개만 반환
            return resultList.stream().limit(5).toList();

        } catch (Exception e) {
            log.error("❌ 네이버 지도 병원 검색 실패: {}", e.getMessage());
            return resultList;
        }
    }
    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

}
