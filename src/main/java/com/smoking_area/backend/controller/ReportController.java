package com.smoking_area.backend.controller;

import com.smoking_area.backend.dto.ReportRequest;
import com.smoking_area.backend.dto.ReportResponse;
import com.smoking_area.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestParam Long userId,
            @RequestBody ReportRequest request
    ) {

        ReportResponse response = reportService.createReport(userId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reportId}/approve")
    public ResponseEntity<ReportResponse> approveReport(
            @PathVariable Long reportId,
            @RequestParam Long adminUserId
    ) {

        ReportResponse response =
                reportService.approveReport(reportId, adminUserId);

        return ResponseEntity.ok(response);
    }
}
