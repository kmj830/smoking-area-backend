package com.smoking_area.backend.repository;

import com.smoking_area.backend.entity.Report;
import com.smoking_area.backend.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>{
    List<Report> findByReportStatus(ReportStatus reportStatus);
}

