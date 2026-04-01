package com.damm.server.infra.publicdata.domain;

import com.damm.server.global.common.BaseTimeEntity;
import com.damm.server.infra.publicdata.domain.enums.ParserType;
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
    private String cityDistrict; // 기초지자체 (예: 송파구, 성남시)

    @Column(nullable = false, length = 500)
    private String baseUrl;   // API 엔드포인트 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParserType parserType;   // 파서 식별자 (각 지역별로 응답 포맷이 다르기 때문, 예: GWANGJIN, SEODAEMUN)

    @Column(nullable = false)
    private boolean active;   // 활성화 여부 (장애 시 false로 끄기 위함)

    private LocalDateTime lastSyncedAt; // 마지막 동기화 성공 시각

    @Builder
    public ApiSource(Province province, String cityDistrict, String baseUrl, ParserType parserType, boolean active) {
        this.province = province;
        this.cityDistrict = cityDistrict;
        this.baseUrl = baseUrl;
        this.parserType = parserType;
        this.active = active;
    }

    public String getFullRegionName() {
        return String.format("%s %s", this.province.getKoreanName(), this.cityDistrict);
    }

    public void updateSyncTime() {
        this.lastSyncedAt = LocalDateTime.now();
    }
}