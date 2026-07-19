package com.smoking_area.backend.controller;

import com.smoking_area.backend.entity.SmokingArea;
import com.smoking_area.backend.service.SmokingAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "흡연구역", description = "흡연구역 조회/검색 API. 인증 불필요 (전체 공개).")
@RestController
@RequestMapping(value = "/api/smoking-areas", produces = "application/json; charset=UTF-8")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SmokingAreaController {
    private final SmokingAreaService smokingAreaService;

    @Operation(summary = "전체 흡연구역 목록 조회", description = "등록된 모든 흡연구역을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<SmokingArea>> getAllSmokingAreas() {
        List<SmokingArea> areas = smokingAreaService.getAllSmokingAreas();
        return ResponseEntity.ok(areas);
    }

    @Operation(summary = "흡연구역 상세 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 흡연구역이 존재하지 않음")
    @GetMapping("/{id}")
    public ResponseEntity<SmokingArea> getSmokingAreaById(
            @Parameter(description = "흡연구역 ID", example = "1") @PathVariable Long id) {
        SmokingArea area = smokingAreaService.getSmokingAreaById(id);
        if (area != null) {
            return ResponseEntity.ok(area);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "키워드로 흡연구역 검색", description = "이름/주소 등에 keyword가 포함된 흡연구역을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공 (결과 없으면 빈 배열)")
    @GetMapping("/search")
    public ResponseEntity<List<SmokingArea>> searchSmokingAreas(
            @Parameter(description = "검색 키워드", example = "강남역") @RequestParam String keyword) {
        List<SmokingArea> searchResults = smokingAreaService.searchSmokingAreas(keyword);
        return ResponseEntity.ok(searchResults);
    }
}
