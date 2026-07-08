package com.smoking_area.backend.controller;

import com.smoking_area.backend.entity.SmokingArea;
import com.smoking_area.backend.service.SmokingAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
