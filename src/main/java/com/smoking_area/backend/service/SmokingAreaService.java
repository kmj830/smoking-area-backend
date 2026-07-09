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

    /**
     * ID로 특정 흡연구역 상세 정보 조회
     */
    public SmokingArea getSmokingAreaById(Long id) {
        return smokingAreaRepository.findById(id).orElse(null);
    }

    /**
     * 키워드로 흡연구역 검색
     */
    public List<SmokingArea> searchSmokingAreas(String keyword) {
        return smokingAreaRepository.findByNameContainingOrAddressContaining(keyword, keyword);
    }
}
