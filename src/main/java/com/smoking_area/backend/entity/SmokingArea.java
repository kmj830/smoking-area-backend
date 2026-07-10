package com.smoking_area.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "smoking_areas")
public class SmokingArea extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Double latitude; //위도

    @Column(nullable = false)
    private Double longitude; //경도

    @Column(nullable = false, length = 200)
    private String address;

    @Column(length = 50)
    private String type; // 흡연구역 형태(개방, 폐쇄, 부스)

    @Column(columnDefinition = "TEXT")
    private String description; // 상세 설명

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status")
    private OperationStatus operationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "congestion_level")
    private CongestionLevel congestionLevel;
}
