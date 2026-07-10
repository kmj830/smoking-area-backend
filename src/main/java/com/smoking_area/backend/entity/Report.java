package com.smoking_area.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reports")
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제보한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 흡연구역인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smoking_area_id", nullable = false)
    private SmokingArea smokingArea;

    // 운영 여부 제보인지 혼잡도 제보인지
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    // 운영 여부
    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;

    // 혼잡도
    @Enumerated(EnumType.STRING)
    private CongestionLevel congestionLevel;

    // 관리자 승인 여부
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus reportStatus = ReportStatus.PENDING;
}
