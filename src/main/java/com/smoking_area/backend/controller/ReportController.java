package com.smoking_area.backend.controller;

import com.smoking_area.backend.dto.ReportRequest;
import com.smoking_area.backend.dto.ReportResponse;
import com.smoking_area.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 제보 등록 직후, 사진을 첨부한다.
     * (createReport는 JSON 바디를 그대로 쓰기 위해 이미지는 별도 엔드포인트로 분리했다.)
     */
    @PostMapping(value = "/{reportId}/image", consumes = "multipart/form-data")
    public ResponseEntity<ReportResponse> attachImage(
            @PathVariable Long reportId,
            @RequestParam Long userId,
            @RequestPart MultipartFile image
    ) {
        ReportResponse response = reportService.attachImage(reportId, userId, image);

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
