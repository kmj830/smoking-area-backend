package com.smoking_area.backend.controller;

import com.smoking_area.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * 프론트엔드가 카카오 로그인 창에서 리다이렉트되어 받은 인가코드(code)를 백엔드로 넘겨주는 곳입니다.
     * 검증 완료 후 최종 발행된 JWT 토큰 객체를 반환합니다.
     */
    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoCallback(@RequestParam String code) {
        String jwtToken = authService.kakaoLogin(code);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwtToken);

        return ResponseEntity.ok(response);
    }
}