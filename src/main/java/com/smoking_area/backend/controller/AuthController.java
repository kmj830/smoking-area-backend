package com.smoking_area.backend.controller;

import com.smoking_area.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * 프론트엔드가 카카오 로그인 창에서 리다이렉트되어 받은 인가코드(code)를 백엔드로 넘겨주는 곳입니다.
     * 검증 완료 후 최종 발행된 JWT 토큰과 유저 프로필 객체를 반환합니다.
     */
    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, Object>> kakaoCallback(@RequestParam String code) {
        // 서비스 단에서 토큰과 유저 정보가 모두 포함된 Map을 받아와 그대로 반환합니다.
        Map<String, Object> response = authService.kakaoLogin(code);
        return ResponseEntity.ok(response);
    }
}