package com.smoking_area.backend.controller;

import com.smoking_area.backend.dto.ErrorResponse;
import com.smoking_area.backend.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "즐겨찾기", description = "로그인한 사용자의 흡연구역 즐겨찾기 API. Authorization: Bearer {JWT} 필요.")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "JWT가 없거나 유효하지 않음",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @Operation(summary = "즐겨찾기 추가")
    @ApiResponse(responseCode = "200", description = "추가 성공")
    @PostMapping("/{smokingAreaId}")
    public ResponseEntity<Map<String, String>> addBookmark(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "흡연구역 ID") @PathVariable Long smokingAreaId) {
        bookmarkService.addBookmark(Long.valueOf(userId), smokingAreaId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "즐겨찾기에 추가되었습니다.");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "즐겨찾기 삭제")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{smokingAreaId}")
    public ResponseEntity<Map<String, String>> removeBookmark(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "흡연구역 ID") @PathVariable Long smokingAreaId) {
        bookmarkService.removeBookmark(Long.valueOf(userId), smokingAreaId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "즐겨찾기에서 삭제되었습니다");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 즐겨찾기 목록 조회", description = "로그인한 사용자가 즐겨찾기한 흡연구역들의 ID 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyBookmarks(@AuthenticationPrincipal String userId) {
        List<Long> smokingAreaIds = bookmarkService.getBookmarkSmokingAreaIds(Long.valueOf(userId));

        Map<String, Object> response = new HashMap<>();
        response.put("smokingAreaIds", smokingAreaIds);
        return ResponseEntity.ok(response);
    }
}
