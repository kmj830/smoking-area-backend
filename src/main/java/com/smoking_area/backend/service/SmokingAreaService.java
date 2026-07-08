package com.smoking_area.backend.service;

import com.smoking_area.backend.entity.SmokingArea;
import com.smoking_area.backend.repository.SmokingAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SmokingAreaService {
    private final SmokingAreaRepository smokingAreaRepository;

    /**
     * 지도에 표시할 전체 흡연구역 목록 조회*/
    public List<SmokingArea> getAllSmokingAreas() {
        return smokingAreaRepository.findAll();
    }
}
