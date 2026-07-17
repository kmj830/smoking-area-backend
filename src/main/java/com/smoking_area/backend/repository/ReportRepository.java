package com.smoking_area.backend.repository;

import com.smoking_area.backend.entity.Report;
import com.smoking_area.backend.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReportStatusOrderByCreatedAtDesc(ReportStatus reportStatus);

    List<Report> findAllByOrderByCreatedAtDesc();
}
