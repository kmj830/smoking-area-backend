package com.smoking_area.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "제보 처리 결과 응답")
public class ReportResponse {

    @Schema(description = "제보 ID", example = "1")
    private Long reportId;

    @Schema(description = "처리 결과 메시지", example = "제보가 접수되었습니다.")
    private String message;
}
