package com.smoking_area.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "흡연구역 혼잡도: LOW(여유), MEDIUM(보통), HIGH(혼잡)")
public enum CongestionLevel {
    LOW,
    MEDIUM,
    HIGH
}
