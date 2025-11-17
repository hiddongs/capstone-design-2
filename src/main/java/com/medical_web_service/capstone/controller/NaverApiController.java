package com.medical_web_service.capstone.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class NaverApiController {

    @GetMapping("/api/naver/local")
    public ResponseEntity<String> searchLocal(@RequestParam String query) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
      
      

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://openapi.naver.com/v1/search/local.json?query=" 
                     + query + "&display=10";

        ResponseEntity<String> response =
                rt.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
    }
}
