package com.smoking_area.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "흡연구역 운영 상태: OPEN(운영중), CLOSED(폐쇄됨), UNKNOWN(확인불가)")
public enum OperationStatus {
    OPEN,
    CLOSED,
    UNKNOWN
}
