package com.smoking_area.backend.service;

import com.smoking_area.backend.dto.ReportRequest;
import com.smoking_area.backend.dto.ReportResponse;
import com.smoking_area.backend.entity.*;
import com.smoking_area.backend.repository.ReportRepository;
import com.smoking_area.backend.repository.SmokingAreaRepository;
import com.smoking_area.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final SmokingAreaRepository smokingAreaRepository;

    public ReportResponse createReport(Long userId, ReportRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        SmokingArea smokingArea = smokingAreaRepository.findById(request.getSmokingAreaId())
                .orElseThrow(() -> new RuntimeException("흡연구역을 찾을 수 없습니다."));

        Report report = new Report();

        report.setUser(user);
        report.setSmokingArea(smokingArea);

        report.setReportType(request.getReportType());

        report.setOperationStatus(request.getOperationStatus());

        report.setCongestionLevel(request.getCongestionLevel());

        report.setReportStatus(ReportStatus.PENDING);

        Report savedReport = reportRepository.save(report);

        return new ReportResponse(
                savedReport.getId(),
                "제보가 완료되었습니다."
        );
    }

    public ReportResponse approveReport(Long reportId, Long adminUserId) {

        // 관리자 조회
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));

        // 관리자 권한 확인
        if (!Objects.equals(admin.getRole(), "ADMIN")) {
            throw new RuntimeException("관리자만 승인할 수 있습니다.");
        }

        // 제보 조회
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("제보를 찾을 수 없습니다."));

        // 이미 승인된 제보인지 확인
        if (report.getReportStatus() == ReportStatus.APPROVED) {
            throw new RuntimeException("이미 승인된 제보입니다.");
        }

        SmokingArea smokingArea = report.getSmokingArea();

        // 운영 여부 제보
        if (report.getReportType() == ReportType.OPERATION) {
            smokingArea.setOperationStatus(report.getOperationStatus());
        }

        // 혼잡도 제보
        if (report.getReportType() == ReportType.CONGESTION) {
            smokingArea.setCongestionLevel(report.getCongestionLevel());
        }

        // 승인 처리
        report.setReportStatus(ReportStatus.APPROVED);

        reportRepository.save(report);

        return new ReportResponse(
                report.getId(),
                "승인이 완료되었습니다."
        );
    }
}
