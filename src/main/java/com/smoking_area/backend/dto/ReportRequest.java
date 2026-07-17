package com.smoking_area.backend.dto;

import com.smoking_area.backend.entity.CongestionLevel;
import com.smoking_area.backend.entity.OperationStatus;
import com.smoking_area.backend.entity.ReportType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportRequest {

    /**
     * 기존 흡연구역 제보에 사용한다.
     * 신규 흡연구역 제보에서는 null이다.
     */
    private Long smokingAreaId;

    /**
     * NEW_SMOKING_AREA, OPERATION, CONGESTION
     */
    private ReportType reportType;

    /**
     * 신규 흡연구역 제보 정보
     */
    private String suggestedName;

    private String address;

    /**
     * 신규로 제보하는 흡연구역 자체의 위치
     */
    private Double latitude;

    private Double longitude;

    /**
     * 운영 여부 제보 정보
     */
    private OperationStatus operationStatus;

    /**
     * 혼잡도 제보 정보
     */
    private CongestionLevel congestionLevel;

    /**
     * 제보 당시 사용자의 현재 위치
     */
    private Double reporterLatitude;

    private Double reporterLongitude;
}