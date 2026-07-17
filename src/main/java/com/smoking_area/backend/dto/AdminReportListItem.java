package com.smoking_area.backend.dto;

import com.smoking_area.backend.entity.CongestionLevel;
import com.smoking_area.backend.entity.OperationStatus;
import com.smoking_area.backend.entity.Report;
import com.smoking_area.backend.entity.ReportStatus;
import com.smoking_area.backend.entity.ReportType;
import lombok.Getter;

@Getter
public class AdminReportListItem {

    private final Long id;
    private final Long userId;
    private final String userNickname;
    private final Long smokingAreaId;
    private final String smokingAreaName;
    private final ReportType reportType;
    private final String suggestedName;
    private final String address;
    private final Double latitude;
    private final Double longitude;
    private final OperationStatus operationStatus;
    private final CongestionLevel congestionLevel;
    private final String imageUrl;
    private final ReportStatus reportStatus;
    private final String rejectReason;

    public AdminReportListItem(Report report) {
        this.id = report.getId();
        this.userId = report.getUser().getId();
        this.userNickname = report.getUser().getNickname();
        this.smokingAreaId = report.getSmokingArea() != null ? report.getSmokingArea().getId() : null;
        this.smokingAreaName = report.getSmokingArea() != null ? report.getSmokingArea().getName() : null;
        this.reportType = report.getReportType();
        this.suggestedName = report.getSuggestedName();
        this.address = report.getAddress();
        this.latitude = report.getLatitude();
        this.longitude = report.getLongitude();
        this.operationStatus = report.getOperationStatus();
        this.congestionLevel = report.getCongestionLevel();
        this.imageUrl = report.getImageUrl();
        this.reportStatus = report.getReportStatus();
        this.rejectReason = report.getRejectReason();
    }
}
