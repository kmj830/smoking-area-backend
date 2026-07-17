package com.smoking_area.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 별도 로그인 없이, 요청 헤더의 관리자 키(X-Admin-Key)를 검증한다.
 * /api/admin/** 하위 API에만 적용된다.
 * (기존 /api/reports/{id}/approve 는 adminUserId + role 체크로 별도 보호된다.)
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Value("${admin.key}")
    private String adminKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String provided = request.getHeader("X-Admin-Key");

        if (provided == null || !provided.equals(adminKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"관리자 인증이 필요합니다.\"}");
            return false;
        }
        return true;
    }
}
