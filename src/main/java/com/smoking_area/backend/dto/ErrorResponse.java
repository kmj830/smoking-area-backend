package com.smoking_area.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 실제 응답 클래스가 아니라, GlobalExceptionHandler가 반환하는
 * Map<String, String> 에러 응답의 형태를 Swagger 문서에 표시하기 위한 전용 클래스입니다.
 * (코드에서 직접 사용하지 않고 @ApiResponse의 schema 참조용으로만 사용)
 */
@Schema(description = "에러 응답 공통 형식")
public class ErrorResponse {

    @Schema(description = "에러 메시지", example = "요청 본문이 올바르지 않습니다.")
    private String message;
}
