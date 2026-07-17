package com.smoking_area.backend.controller;

import com.smoking_area.backend.dto.AdminReportListItem;
import com.smoking_area.backend.dto.ReportRejectRequest;
import com.smoking_area.backend.dto.ReportResponse;
import com.smoking_area.backend.entity.Report;
import com.smoking_area.backend.entity.ReportStatus;
import com.smoking_area.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    /**
     * 어드민 페이지 로그인(키) 검증용.
     */
    @GetMapping("/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AdminReportListItem>> getReports(
            @RequestParam(required = false) ReportStatus status
    ) {
        List<AdminReportListItem> result = reportService.listReports(status).stream()
                .map(AdminReportListItem::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<AdminReportListItem> getReport(@PathVariable Long reportId) {
        Report report = reportService.getReport(reportId);
        return ResponseEntity.ok(new AdminReportListItem(report));
    }

    @PatchMapping("/{reportId}/approve")
    public ResponseEntity<ReportResponse> approve(
            @PathVariable Long reportId,
            @RequestParam Long adminUserId
    ) {
        ReportResponse response = reportService.approveReport(reportId, adminUserId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<ReportResponse> reject(
            @PathVariable Long reportId,
            @RequestParam Long adminUserId,
            @RequestBody(required = false) ReportRejectRequest request
    ) {
        String reason = request != null ? request.getReason() : null;
        ReportResponse response = reportService.rejectReport(reportId, adminUserId, reason);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}
