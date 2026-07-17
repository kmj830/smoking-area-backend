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

    /**
     * 제보한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 기존 흡연구역 제보에서는 값이 존재한다.
     * 신규 흡연구역 제보에서는 아직 등록된 흡연구역이 없으므로 null이다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smoking_area_id")
    private SmokingArea smokingArea;

    /**
     * 제보 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 30)
    private ReportType reportType;

    /**
     * 신규 흡연구역 제보 정보
     */
    @Column(name = "suggested_name", length = 100)
    private String suggestedName;

    @Column(name = "address", length = 200)
    private String address;

    /**
     * 신규로 제보하는 흡연구역 자체의 위치
     */
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    /**
     * 제보 당시 사용자의 위치
     */
    @Column(name = "reporter_latitude")
    private Double reporterLatitude;

    @Column(name = "reporter_longitude")
    private Double reporterLongitude;

    /**
     * 운영 여부 제보 정보
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", length = 30)
    private OperationStatus operationStatus;

    /**
     * 혼잡도 제보 정보
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "congestion_level", length = 30)
    private CongestionLevel congestionLevel;

    /**
     * 관리자 처리 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false, length = 30)
    private ReportStatus reportStatus = ReportStatus.PENDING;
}