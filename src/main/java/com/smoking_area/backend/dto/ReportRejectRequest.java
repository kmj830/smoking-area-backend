package com.smoking_area.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "제보 반려 요청")
public class ReportRejectRequest {

    @Schema(description = "반려 사유 (선택, 생략 가능)", example = "위치 정보가 부정확합니다.")
    private String reason;
}
