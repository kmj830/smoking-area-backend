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

import java.util.HashMap;
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
     * 카카오 로그인 전체 흐름 처리 (토큰 받기 -> 유저정보 조회 -> DB 저장 -> 자체 JWT 발급 및 유저 정보 반환)
     */
    public Map<String, Object> kakaoLogin(String code) {
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
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .kakaoId(kakaoId)
                                .nickname(nickname)
                                .role("USER")
                                .build()
                ));

        // 5. 우리 서비스 전용 자체 JWT 토큰 생성
        String jwtToken = jwtTokenProvider.createToken(user.getId(), user.getNickname());

        // 6. 프론트엔드(Next.js) 규격에 맞춰 token과 user 오브젝트를 함께 응답 맵에 구성
        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtToken);

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("id", user.getKakaoId()); // 화면의 '카카오 ID' 영역에 매핑하기 위해 실제 kakaoId 주입
        userProfile.put("nickname", user.getNickname()); // 실제 카카오 닉네임 주입

        result.put("user", userProfile);

        return result;
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