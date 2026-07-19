package com.smoking_area.backend.dto;

import com.smoking_area.backend.entity.CongestionLevel;
import com.smoking_area.backend.entity.OperationStatus;
import com.smoking_area.backend.entity.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "제보 등록 요청. reportType에 따라 필요한 필드가 다릅니다 (ReportType 설명 참고).")
public class ReportRequest {

    @Schema(description = "기존 흡연구역 제보(OPERATION, CONGESTION)에 사용. 신규 제보(NEW_SMOKING_AREA)에서는 null.", example = "12")
    private Long smokingAreaId;

    @Schema(description = "제보 종류", requiredMode = Schema.RequiredMode.REQUIRED)
    private ReportType reportType;

    @Schema(description = "[신규 제보용] 제안하는 흡연구역 이름", example = "회사 앞 흡연부스")
    private String suggestedName;

    @Schema(description = "[신규 제보용] 주소", example = "세종특별자치시 어진동 000")
    private String address;

    @Schema(description = "[신규 제보용] 제보하는 흡연구역 자체의 위도", example = "36.4800")
    private Double latitude;

    @Schema(description = "[신규 제보용] 제보하는 흡연구역 자체의 경도", example = "127.2890")
    private Double longitude;

    @Schema(description = "[운영 여부 제보용] 운영 상태")
    private OperationStatus operationStatus;

    @Schema(description = "[혼잡도 제보용] 혼잡도")
    private CongestionLevel congestionLevel;

    @Schema(description = "제보 당시 사용자의 현재 위도 (통계/검증용, 선택)", example = "36.4801")
    private Double reporterLatitude;

    @Schema(description = "제보 당시 사용자의 현재 경도 (통계/검증용, 선택)", example = "127.2891")
    private Double reporterLongitude;
}
