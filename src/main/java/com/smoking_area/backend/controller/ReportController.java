package com.smoking_area.backend.controller;

import com.smoking_area.backend.dto.ErrorResponse;
import com.smoking_area.backend.dto.ReportRequest;
import com.smoking_area.backend.dto.ReportResponse;
import com.smoking_area.backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "제보", description = "흡연구역 신규/운영여부/혼잡도 제보 API. " +
        "주의: 로그인(JWT)이 필요하지만, 서버가 사용자를 식별할 때는 토큰이 아니라 " +
        "요청의 userId 파라미터를 그대로 사용합니다. 즉 유효한 Bearer 토큰과 userId 둘 다 보내야 합니다.")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "JWT가 없거나 유효하지 않음",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {
    private final ReportService reportService;

    @Operation(
            summary = "제보 등록",
            description = "신규 흡연구역/운영여부/혼잡도 제보를 등록합니다. " +
                    "reportType에 따라 request의 필수 필드가 달라집니다 (ReportRequest 스키마 참고)."
    )
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @Parameter(description = "제보하는 사용자 ID", example = "5") @RequestParam Long userId,
            @RequestBody ReportRequest request
    ) {

        ReportResponse response = reportService.createReport(userId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "제보에 사진 첨부",
            description = "제보 등록(createReport) 직후, 별도로 사진을 첨부합니다. " +
                    "createReport가 JSON 바디를 쓰기 때문에 이미지 업로드는 이 엔드포인트로 분리되어 있습니다. " +
                    "Content-Type: multipart/form-data 로 호출해야 합니다."
    )
    @ApiResponse(responseCode = "200", description = "첨부 성공")
    @ApiResponse(responseCode = "400", description = "이미지 형식이 올바르지 않거나 reportId가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/{reportId}/image", consumes = "multipart/form-data")
    public ResponseEntity<ReportResponse> attachImage(
            @Parameter(description = "제보 ID") @PathVariable Long reportId,
            @Parameter(description = "제보한 사용자 ID") @RequestParam Long userId,
            @Parameter(description = "첨부할 이미지 파일") @RequestPart MultipartFile image
    ) {
        ReportResponse response = reportService.attachImage(reportId, userId, image);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "제보 승인",
            description = "관리자가 제보를 승인합니다. 참고: /api/admin/reports/{reportId}/approve 와 " +
                    "동일한 서비스 로직을 호출하는 별도 경로입니다. 프론트에서 관리자 화면을 만든다면 " +
                    "어느 쪽을 쓸지 팀 내에서 확인하는 것을 권장합니다."
    )
    @ApiResponse(responseCode = "200", description = "승인 성공")
    @ApiResponse(responseCode = "400", description = "adminUserId에 관리자 권한이 없거나 reportId가 존재하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{reportId}/approve")
    public ResponseEntity<ReportResponse> approveReport(
            @Parameter(description = "제보 ID") @PathVariable Long reportId,
            @Parameter(description = "승인 처리하는 관리자 사용자 ID") @RequestParam Long adminUserId
    ) {

        ReportResponse response =
                reportService.approveReport(reportId, adminUserId);

        return ResponseEntity.ok(response);
    }
}
