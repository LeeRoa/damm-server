package com.damm.server.modules.area.domain;

import com.damm.server.global.common.BaseTimeEntity;
import com.damm.server.modules.area.domain.enums.AreaStatus;
import com.damm.server.modules.area.domain.enums.AreaType;
import com.damm.server.modules.area.domain.vo.Address;
import com.damm.server.modules.area.domain.vo.Coordinate;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "smoking_areas", indexes = {
        // Bounding Box 검색 성능을 극대화하기 위해 위도, 경도에 복합 인덱스 설정
        @Index(name = "idx_coordinate", columnList = "latitude, longitude")
})
public class SmokingArea extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String apiId; // 공공데이터의 고유 ID (예: 군자동-02-01-020)

    @Column(nullable = false, length = 100)
    private String name; // 흡연구역명

    @Column(length = 500)
    private String description; // 흡연구역 범위 상세

    @Embedded
    private Coordinate coordinate; // 위도, 경도 VO

    @Embedded
    private Address address; // 주소 VO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AreaType type; // 흡연구역 구분

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AreaStatus status; // 운영 상태

    private Double areaSize; // 면적 (nullable)

    @Column(length = 1000)
    private String imageUrl; // 시설 이미지 URL

    @Builder
    public SmokingArea(String apiId, String name, String description, Coordinate coordinate,
                       Address address, AreaType type, AreaStatus status, Double areaSize, String imageUrl) {
        this.apiId = apiId;
        this.name = name;
        this.description = description;
        this.coordinate = coordinate;
        this.address = address;
        this.type = type;
        this.status = status;
        this.areaSize = areaSize;
        this.imageUrl = imageUrl;
    }
}