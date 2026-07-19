package com.smoking_area.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI에서 "Authorize" 버튼으로 인증 토큰을 입력할 수 있도록
 * 이 프로젝트에서 쓰는 두 가지 인증 방식을 등록한다.
 *
 * 1) bearerAuth : 일반 사용자 API (/api/bookmarks/**, /api/reports/** 등)
 *    - Authorization: Bearer {JWT} 헤더 필요
 * 2) adminKey    : 관리자 API (/api/admin/**)
 *    - X-Admin-Key 헤더 필요 (JWT 아님, AdminAuthInterceptor가 검증)
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";
    private static final String ADMIN_KEY_SCHEME = "adminKey";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smoking Area API")
                        .description("흡연구역 조회/제보/즐겨찾기 API 명세입니다. " +
                                "일반 사용자 API는 카카오 로그인으로 발급받은 JWT를, " +
                                "관리자 API는 X-Admin-Key 헤더를 사용합니다.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("카카오 로그인(/api/auth/kakao/callback) 응답으로 받은 JWT를 입력하세요. " +
                                        "'Bearer ' 접두어는 Swagger UI가 자동으로 붙여줍니다."))
                        .addSecuritySchemes(ADMIN_KEY_SCHEME, new SecurityScheme()
                                .name("X-Admin-Key")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("관리자 페이지 접근용 키입니다. JWT가 아닙니다.")))
                // 기본적으로는 인증을 요구하지 않고, 필요한 컨트롤러/메서드에만 @SecurityRequirement로 개별 지정합니다.
                .security(java.util.List.of());
    }
}
