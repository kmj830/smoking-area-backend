package com.smoking_area.backend.repository;

import com.smoking_area.backend.entity.SmokingArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmokingAreaRepository
        extends JpaRepository<SmokingArea, Long> {

    List<SmokingArea> findByNameContainingOrAddressContaining(
            String nameKeyword,
            String addressKeyword
    );
}