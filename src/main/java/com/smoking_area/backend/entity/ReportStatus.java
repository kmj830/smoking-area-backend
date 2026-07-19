package com.smoking_area.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "제보 처리 상태: PENDING(대기) → APPROVED(승인) 또는 REJECTED(반려)")
public enum ReportStatus {
    PENDING,
    APPROVED,
    REJECTED
}
