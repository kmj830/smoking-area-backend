package com.smoking_area.backend.controller;

import com.smoking_area.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "인증", description = "카카오 로그인 API. 인증 불필요.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "카카오 로그인 콜백",
            description = "프론트엔드가 카카오 로그인 창에서 리다이렉트되어 받은 인가코드(code)를 백엔드로 전달합니다. " +
                    "서버는 코드를 카카오 서버에 검증하고, 이후 API 호출에 쓸 JWT와 유저 프로필을 함께 반환합니다. " +
                    "응답의 'token' 값을 이후 요청의 Authorization 헤더에 'Bearer {token}' 형태로 담아 사용하세요."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공 (JWT + 유저 정보 반환)")
    @ApiResponse(responseCode = "400", description = "인가코드가 유효하지 않음")
    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, Object>> kakaoCallback(
            @Parameter(description = "카카오 로그인 리다이렉트로 받은 인가코드") @RequestParam String code) {
        Map<String, Object> response = authService.kakaoLogin(code);
        return ResponseEntity.ok(response);
    }
}
