package com.smoking_area.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportResponse {
    private Long reportId;

    private String message;
}
