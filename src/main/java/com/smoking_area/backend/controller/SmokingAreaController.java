package com.smoking_area.backend.controller;

import com.smoking_area.backend.entity.SmokingArea;
import com.smoking_area.backend.service.SmokingAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/smoking-areas", produces = "application/json; charset=UTF-8")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SmokingAreaController {
    private final SmokingAreaService smokingAreaService;

    /**
     * 전체 흡연구역 목록을 JSON으로 반환
     */
    @GetMapping
    public ResponseEntity<List<SmokingArea>> getAllSmokingAreas() {
        List<SmokingArea> areas = smokingAreaService.getAllSmokingAreas();
        return ResponseEntity.ok(areas);
    }

    /**
     * 흡연구역 상세 정보 반환*/
    @GetMapping("/{id}")
    public ResponseEntity<SmokingArea> getSmokingAreaById(@PathVariable Long id) {
        SmokingArea area = smokingAreaService.getSmokingAreaById(id);
        if (area != null) {
            return ResponseEntity.ok(area);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 키워드로 흡연구역 검색*/
    @GetMapping("/search")
    public ResponseEntity<List<SmokingArea>> searchSmokingAreas(@RequestParam String keyword) {
        List<SmokingArea> searchResults = smokingAreaService.searchSmokingAreas(keyword);
        return ResponseEntity.ok(searchResults);
    }
}
