package com.smoking_area.backend.controller;

import com.smoking_area.backend.dto.AdminReportListItem;
import com.smoking_area.backend.dto.ErrorResponse;
import com.smoking_area.backend.dto.ReportRejectRequest;
import com.smoking_area.backend.dto.ReportResponse;
import com.smoking_area.backend.entity.Report;
import com.smoking_area.backend.entity.ReportStatus;
import com.smoking_area.backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "관리자-제보", description = "관리자용 제보 처리 API. JWT가 아니라 X-Admin-Key 헤더로 인증합니다.")
@SecurityRequirement(name = "adminKey")
@ApiResponse(responseCode = "401", description = "X-Admin-Key가 없거나 올바르지 않음",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @Operation(summary = "관리자 인증 확인용 ping", description = "X-Admin-Key가 유효한지만 확인합니다. 응답 바디는 없습니다.")
    @ApiResponse(responseCode = "200", description = "키가 유효함")
    @GetMapping("/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "제보 목록 조회", description = "status로 필터링할 수 있습니다. 생략하면 전체 조회.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<AdminReportListItem>> getReports(
            @Parameter(description = "필터링할 처리 상태 (생략 시 전체)") @RequestParam(required = false) ReportStatus status
    ) {
        List<AdminReportListItem> result = reportService.listReports(status).stream()
                .map(AdminReportListItem::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "제보 상세 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "해당 ID의 제보가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{reportId}")
    public ResponseEntity<AdminReportListItem> getReport(
            @Parameter(description = "제보 ID") @PathVariable Long reportId) {
        Report report = reportService.getReport(reportId);
        return ResponseEntity.ok(new AdminReportListItem(report));
    }

    @Operation(summary = "제보 승인")
    @ApiResponse(responseCode = "200", description = "승인 성공")
    @ApiResponse(responseCode = "400", description = "reportId가 존재하지 않거나 이미 처리됨",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{reportId}/approve")
    public ResponseEntity<ReportResponse> approve(
            @Parameter(description = "제보 ID") @PathVariable Long reportId,
            @Parameter(description = "승인 처리하는 관리자 사용자 ID") @RequestParam Long adminUserId
    ) {
        ReportResponse response = reportService.approveReport(reportId, adminUserId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "제보 반려", description = "reason은 선택 값이며, 바디를 아예 생략해도 됩니다.")
    @ApiResponse(responseCode = "200", description = "반려 성공")
    @ApiResponse(responseCode = "400", description = "reportId가 존재하지 않거나 이미 처리됨",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<ReportResponse> reject(
            @Parameter(description = "제보 ID") @PathVariable Long reportId,
            @Parameter(description = "반려 처리하는 관리자 사용자 ID") @RequestParam Long adminUserId,
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
