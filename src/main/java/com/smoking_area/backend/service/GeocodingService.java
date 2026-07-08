package com.smoking_area.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeocodingService {

    private final String KAKAO_REST_API_KEY = "ad6819c72b512ad17b6b7ebd4210ead1";

    /**
     * 텍스트 주소를 받아 위도(latitude)와 경도(longitude) 맵을 반환합니다.
     */
    public Map<String, Double> getCoordinates(String address) {
        Map<String, Double> coordinates = new HashMap<>();

        // API 호출 실패 시 에러 방지를 위한 기본값 (서울 중심점) 세팅
        coordinates.put("latitude", 37.5665);
        coordinates.put("longitude", 126.9780);

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 💡 해결 완료: fromHttpUrl 대신 fromUriString을 사용합니다.
            URI uri = UriComponentsBuilder
                    .fromUriString("https://dapi.kakao.com/v2/local/search/address.json")
                    .queryParam("query", address)
                    .build()
                    .encode()
                    .toUri();

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            // JSON 결과 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            // 검색 결과가 존재하는 경우
            if (documents.isArray() && documents.size() > 0) {
                JsonNode firstAddress = documents.get(0);

                // 카카오 API 결과에서 x는 경도(longitude), y는 위도(latitude)입니다.
                double longitude = Double.parseDouble(firstAddress.path("x").asText());
                double latitude = Double.parseDouble(firstAddress.path("y").asText());

                coordinates.put("latitude", latitude);
                coordinates.put("longitude", longitude);
            }
        } catch (Exception e) {
            System.out.println("[Geocoding Error] 주소 변환 실패 -> 주소: " + address + " / 원인: " + e.getMessage());
        }

        return coordinates;
    }
}