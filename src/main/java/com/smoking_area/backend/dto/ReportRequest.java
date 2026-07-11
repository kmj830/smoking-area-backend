package com.smoking_area.backend.dto;

import com.smoking_area.backend.entity.CongestionLevel;
import com.smoking_area.backend.entity.OperationStatus;
import com.smoking_area.backend.entity.ReportType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {

    // 어떤 흡연구역인지
    private Long smokingAreaId;

    // OPERATION or CONGESTION
    private ReportType reportType;

    // 운영 여부
    private OperationStatus operationStatus;

    // 혼잡도
    private CongestionLevel congestionLevel;
}
