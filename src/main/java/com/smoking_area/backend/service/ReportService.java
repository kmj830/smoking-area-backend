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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final SmokingAreaRepository smokingAreaRepository;

    /**
     * 제보 등록
     */
    @Transactional
    public ReportResponse createReport(
            Long userId,
            ReportRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("사용자를 찾을 수 없습니다.")
                );

        if (request.getReportType() == null) {
            throw new RuntimeException("제보 유형은 필수입니다.");
        }

        /*
         * 모든 제보에서 제보 당시 사용자의 위치를 필수로 받는다.
         */
        validateCoordinates(
                request.getReporterLatitude(),
                request.getReporterLongitude(),
                "제보자 위치"
        );

        Report report = new Report();

        report.setUser(user);
        report.setReportType(request.getReportType());
        report.setReportStatus(ReportStatus.PENDING);

        /*
         * 제보 당시 사용자의 위치 저장
         */
        report.setReporterLatitude(
                request.getReporterLatitude()
        );
        report.setReporterLongitude(
                request.getReporterLongitude()
        );

        switch (request.getReportType()) {
            case NEW_SMOKING_AREA:
                createNewSmokingAreaReport(report, request);
                break;

            case OPERATION:
                createOperationReport(report, request);
                break;

            case CONGESTION:
                createCongestionReport(report, request);
                break;

            default:
                throw new RuntimeException(
                        "지원하지 않는 제보 유형입니다."
                );
        }

        Report savedReport = reportRepository.save(report);

        return new ReportResponse(
                savedReport.getId(),
                "제보가 완료되었습니다."
        );
    }

    /**
     * 신규 흡연구역 제보 생성
     */
    private void createNewSmokingAreaReport(
            Report report,
            ReportRequest request
    ) {
        if (request.getSmokingAreaId() != null) {
            throw new RuntimeException(
                    "신규 흡연구역 제보에는 smokingAreaId를 입력할 수 없습니다."
            );
        }

        if (request.getSuggestedName() == null
                || request.getSuggestedName().isBlank()) {
            throw new RuntimeException(
                    "신규 흡연구역 이름이 필요합니다."
            );
        }

        if (request.getAddress() == null
                || request.getAddress().isBlank()) {
            throw new RuntimeException(
                    "신규 흡연구역 주소가 필요합니다."
            );
        }

        /*
         * 신규로 등록할 흡연구역 자체의 위치를 검사한다.
         */
        validateCoordinates(
                request.getLatitude(),
                request.getLongitude(),
                "신규 흡연구역"
        );

        if (request.getOperationStatus() != null) {
            throw new RuntimeException(
                    "신규 흡연구역 제보에는 운영 상태를 입력할 수 없습니다."
            );
        }

        if (request.getCongestionLevel() != null) {
            throw new RuntimeException(
                    "신규 흡연구역 제보에는 혼잡도를 입력할 수 없습니다."
            );
        }

        report.setSmokingArea(null);

        report.setSuggestedName(request.getSuggestedName());
        report.setAddress(request.getAddress());
        report.setLatitude(request.getLatitude());
        report.setLongitude(request.getLongitude());

        report.setOperationStatus(null);
        report.setCongestionLevel(null);
    }

    /**
     * 기존 흡연구역 운영 여부 제보 생성
     */
    private void createOperationReport(
            Report report,
            ReportRequest request
    ) {
        SmokingArea smokingArea =
                getSmokingArea(request.getSmokingAreaId());

        if (request.getOperationStatus() == null) {
            throw new RuntimeException(
                    "운영 여부 제보에는 운영 상태가 필요합니다."
            );
        }

        if (request.getCongestionLevel() != null) {
            throw new RuntimeException(
                    "운영 여부 제보에는 혼잡도를 입력할 수 없습니다."
            );
        }

        validateNewSmokingAreaFieldsAreEmpty(request);

        report.setSmokingArea(smokingArea);
        report.setOperationStatus(request.getOperationStatus());
        report.setCongestionLevel(null);

        clearNewSmokingAreaFields(report);
    }

    /**
     * 기존 흡연구역 혼잡도 제보 생성
     */
    private void createCongestionReport(
            Report report,
            ReportRequest request
    ) {
        SmokingArea smokingArea =
                getSmokingArea(request.getSmokingAreaId());

        if (request.getCongestionLevel() == null) {
            throw new RuntimeException(
                    "혼잡도 제보에는 혼잡도 정보가 필요합니다."
            );
        }

        if (request.getOperationStatus() != null) {
            throw new RuntimeException(
                    "혼잡도 제보에는 운영 상태를 입력할 수 없습니다."
            );
        }

        validateNewSmokingAreaFieldsAreEmpty(request);

        report.setSmokingArea(smokingArea);
        report.setCongestionLevel(request.getCongestionLevel());
        report.setOperationStatus(null);

        clearNewSmokingAreaFields(report);
    }

    /**
     * 관리자 제보 승인
     */
    @Transactional
    public ReportResponse approveReport(
            Long reportId,
            Long adminUserId
    ) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() ->
                        new RuntimeException("관리자를 찾을 수 없습니다.")
                );

        if (!"ADMIN".equals(String.valueOf(admin.getRole()))) {
            throw new RuntimeException(
                    "관리자만 승인할 수 있습니다."
            );
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new RuntimeException("제보를 찾을 수 없습니다.")
                );

        if (report.getReportStatus() != ReportStatus.PENDING) {
            throw new RuntimeException(
                    "이미 처리된 제보입니다."
            );
        }

        switch (report.getReportType()) {
            case NEW_SMOKING_AREA:
                approveNewSmokingAreaReport(report);
                break;

            case OPERATION:
                approveOperationReport(report);
                break;

            case CONGESTION:
                approveCongestionReport(report);
                break;

            default:
                throw new RuntimeException(
                        "지원하지 않는 제보 유형입니다."
                );
        }

        report.setReportStatus(ReportStatus.APPROVED);

        return new ReportResponse(
                report.getId(),
                "승인이 완료되었습니다."
        );
    }

    /**
     * 신규 흡연구역 제보 승인
     */
    private void approveNewSmokingAreaReport(Report report) {
        if (report.getSuggestedName() == null
                || report.getSuggestedName().isBlank()) {
            throw new RuntimeException(
                    "신규 흡연구역 이름이 없습니다."
            );
        }

        if (report.getAddress() == null
                || report.getAddress().isBlank()) {
            throw new RuntimeException(
                    "신규 흡연구역 주소가 없습니다."
            );
        }

        validateCoordinates(
                report.getLatitude(),
                report.getLongitude(),
                "신규 흡연구역"
        );

        SmokingArea smokingArea = new SmokingArea();

        smokingArea.setName(report.getSuggestedName());
        smokingArea.setAddress(report.getAddress());
        smokingArea.setLatitude(report.getLatitude());
        smokingArea.setLongitude(report.getLongitude());

        SmokingArea savedSmokingArea =
                smokingAreaRepository.save(smokingArea);

        report.setSmokingArea(savedSmokingArea);
    }

    /**
     * 운영 여부 제보 승인
     */
    private void approveOperationReport(Report report) {
        SmokingArea smokingArea =
                getReportSmokingArea(report);

        if (report.getOperationStatus() == null) {
            throw new RuntimeException(
                    "제보된 운영 상태가 없습니다."
            );
        }

        smokingArea.setOperationStatus(
                report.getOperationStatus()
        );
    }

    /**
     * 혼잡도 제보 승인
     */
    private void approveCongestionReport(Report report) {
        SmokingArea smokingArea =
                getReportSmokingArea(report);

        if (report.getCongestionLevel() == null) {
            throw new RuntimeException(
                    "제보된 혼잡도 정보가 없습니다."
            );
        }

        smokingArea.setCongestionLevel(
                report.getCongestionLevel()
        );
    }

    /**
     * ID로 흡연구역 조회
     */
    private SmokingArea getSmokingArea(Long smokingAreaId) {
        if (smokingAreaId == null) {
            throw new RuntimeException(
                    "기존 흡연구역 제보에는 smokingAreaId가 필요합니다."
            );
        }

        return smokingAreaRepository.findById(smokingAreaId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "흡연구역을 찾을 수 없습니다."
                        )
                );
    }

    /**
     * Report에 연결된 흡연구역 반환
     */
    private SmokingArea getReportSmokingArea(Report report) {
        if (report.getSmokingArea() == null) {
            throw new RuntimeException(
                    "제보에 연결된 흡연구역이 없습니다."
            );
        }

        return report.getSmokingArea();
    }

    /**
     * 위도·경도 검증
     */
    private void validateCoordinates(
            Double latitude,
            Double longitude,
            String target
    ) {
        if (latitude == null || longitude == null) {
            throw new RuntimeException(
                    target + "의 위도와 경도가 필요합니다."
            );
        }

        if (latitude < -90 || latitude > 90) {
            throw new RuntimeException(
                    target + "의 위도는 -90 이상 90 이하여야 합니다."
            );
        }

        if (longitude < -180 || longitude > 180) {
            throw new RuntimeException(
                    target + "의 경도는 -180 이상 180 이하여야 합니다."
            );
        }
    }

    /**
     * 기존 흡연구역 제보에 신규 흡연구역 정보가 들어왔는지 검사한다.
     *
     * reporterLatitude, reporterLongitude는 제보자 위치이므로
     * 여기에서 검사하지 않는다.
     */
    private void validateNewSmokingAreaFieldsAreEmpty(
            ReportRequest request
    ) {
        if (request.getSuggestedName() != null
                || request.getAddress() != null
                || request.getLatitude() != null
                || request.getLongitude() != null) {
            throw new RuntimeException(
                    "기존 흡연구역 제보에는 신규 흡연구역 정보를 입력할 수 없습니다."
            );
        }
    }

    private void clearNewSmokingAreaFields(Report report) {
        report.setSuggestedName(null);
        report.setAddress(null);
        report.setLatitude(null);
        report.setLongitude(null);
    }
}