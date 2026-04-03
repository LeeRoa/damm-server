package com.damm.server.infra.publicdata.domain;

import com.damm.server.global.common.BaseTimeEntity;
import com.damm.server.infra.publicdata.domain.enums.District;
import com.damm.server.infra.publicdata.domain.enums.Province;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "api_sources")
public class ApiSource extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Province province;  // 광역지자체 (예: 서울특별시, 경기도)

    @Column(nullable = false)
    private District cityDistrict; // 기초지자체 (예: 송파구, 성남시)

    @Column(nullable = false, length = 500)
    private String baseUrl;   // API 엔드포인트 주소

    @Column(nullable = false)
    private boolean active;   // 활성화 여부 (장애 시 false로 끄기 위함)

    private LocalDateTime lastSyncedAt; // 마지막 동기화 성공 시각

    @Builder
    public ApiSource(Province province, District cityDistrict, String baseUrl, boolean active) {
        this.province = province;
        this.cityDistrict = cityDistrict;
        this.baseUrl = baseUrl;
        this.active = active;
    }

    public String getFullRegionName() {
        return String.format("%s %s", this.province.getKoreanName(), this.cityDistrict.getKoreanName());
    }

    public void updateSyncTime() {
        this.lastSyncedAt = LocalDateTime.now();
    }
}