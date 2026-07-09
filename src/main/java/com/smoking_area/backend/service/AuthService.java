package com.smoking_area.backend.service;

import com.smoking_area.backend.entity.User;
import com.smoking_area.backend.repository.UserRepository;
import com.smoking_area.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 카카오 로그인 전체 흐름 처리 (토큰 받기 -> 유저정보 조회 -> DB 저장 -> 자체 JWT 발급)
     */
    public String kakaoLogin(String code) {
        // 1. 인가 코드로 카카오 Access Token 발급 요청
        String accessToken = getKakaoAccessToken(code);

        // 2. Access Token으로 카카오 사용자 정보 조회
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        // 3. 사용자 정보 파싱
        Long kakaoId = (Long) userInfo.get("id");

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        String nickname = (String) properties.get("nickname");

        // 4. DB 확인 후 가입 또는 기존 유저 정보 조회
        // 기존 엔티티의 필수 필드인 role에 기본값 "USER"를 넘겨주도록 수정했습니다.
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .kakaoId(kakaoId)
                                .nickname(nickname)
                                .role("USER")
                                .build()
                ));

        // 5. 우리 서비스 전용 자체 JWT 토큰 생성 후 반환
        return jwtTokenProvider.createToken(user.getId(), user.getNickname());
    }

    /**
     * 카카오 Access Token 요청 API 통신
     */
    private String getKakaoAccessToken(String code) {
        WebClient webClient = WebClient.create("https://kauth.kakao.com");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && response.containsKey("access_token")) {
            return (String) response.get("access_token");
        }
        throw new RuntimeException("카카오 Access Token을 발급받지 못했습니다.");
    }

    /**
     * 카카오 사용자 정보 요청 API 통신
     */
    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        WebClient webClient = WebClient.create("https://kapi.kakao.com");

        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null) {
            return response;
        }
        throw new RuntimeException("카카오 서버로부터 사용자 정보를 불러오지 못했습니다.");
    }
}