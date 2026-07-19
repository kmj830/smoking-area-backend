package com.smoking_area.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        제보 종류에 따라 ReportRequest에서 채워야 하는 필드가 달라집니다.
        - NEW_SMOKING_AREA: 신규 흡연구역 제보 → suggestedName, address, latitude, longitude 필수 (smokingAreaId는 null)
        - OPERATION: 기존 흡연구역의 운영 여부 제보 → smokingAreaId, operationStatus 필수
        - CONGESTION: 기존 흡연구역의 혼잡도 제보 → smokingAreaId, congestionLevel 필수
        """)
public enum ReportType {
    NEW_SMOKING_AREA,
    OPERATION,
    CONGESTION
}
