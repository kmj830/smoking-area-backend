package com.smoking_area.backend.dto;

import com.smoking_area.backend.entity.CongestionLevel;
import com.smoking_area.backend.entity.OperationStatus;
import com.smoking_area.backend.entity.Report;
import com.smoking_area.backend.entity.ReportStatus;
import com.smoking_area.backend.entity.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "관리자용 제보 상세/목록 조회 응답")
public class AdminReportListItem {

    @Schema(description = "제보 ID", example = "1")
    private final Long id;
    @Schema(description = "제보한 사용자 ID", example = "5")
    private final Long userId;
    @Schema(description = "제보한 사용자 닉네임", example = "홍길동")
    private final String userNickname;
    @Schema(description = "대상 흡연구역 ID (신규 제보인 경우 null)", example = "12")
    private final Long smokingAreaId;
    @Schema(description = "대상 흡연구역 이름 (신규 제보인 경우 null)", example = "회사 앞 흡연부스")
    private final String smokingAreaName;
    @Schema(description = "제보 종류")
    private final ReportType reportType;
    @Schema(description = "[신규 제보] 제안 이름")
    private final String suggestedName;
    @Schema(description = "[신규 제보] 주소")
    private final String address;
    @Schema(description = "[신규 제보] 위도")
    private final Double latitude;
    @Schema(description = "[신규 제보] 경도")
    private final Double longitude;
    @Schema(description = "[운영 여부 제보] 운영 상태")
    private final OperationStatus operationStatus;
    @Schema(description = "[혼잡도 제보] 혼잡도")
    private final CongestionLevel congestionLevel;
    @Schema(description = "첨부 이미지 URL", example = "https://res.cloudinary.com/.../image.jpg")
    private final String imageUrl;
    @Schema(description = "현재 처리 상태")
    private final ReportStatus reportStatus;
    @Schema(description = "반려 사유 (반려된 경우에만 값 존재)")
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
