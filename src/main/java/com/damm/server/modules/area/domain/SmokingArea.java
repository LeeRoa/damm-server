package com.damm.server.modules.area.domain;

import com.damm.server.global.common.BaseTimeEntity;
import com.damm.server.infra.kakao.dto.GeocodingResponse;
import com.damm.server.modules.area.domain.enums.AddressStatus;
import com.damm.server.modules.area.domain.enums.AreaStatus;
import com.damm.server.modules.area.domain.enums.AreaType;
import com.damm.server.modules.area.domain.vo.Address;
import com.damm.server.modules.area.domain.vo.Coordinate;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 흡연구역 도메인 엔티티.
 * 공공데이터 및 사용자 제보 기반의 흡연구역 핵심 정보를 담는다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "smoking_areas", indexes = {
        @Index(name = "idx_coordinate", columnList = "latitude, longitude")
})
public class SmokingArea extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId; // 내부 관리용 PK

    /**
     * API 항목: id (흡연구역 아이디)
     */
    @Column(unique = true, length = 50)
    private String id;

    /**
     * API 항목: area_nm (흡연구역명)
     */
    @Column(nullable = false, length = 100)
    private String areaNm;

    /**
     * API 항목: area_desc (흡연구역범위상세)
     */
    @Column(length = 500)
    private String areaDesc;

    @Embedded
    private Coordinate coordinate; // latitude, longitude는 동일하므로 유지

    @Embedded
    private Address address;

    /**
     * API 항목: area_se (흡연구역구분)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AreaType areaSe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AreaStatus status; // 내부 운영 상태는 유지

    /**
     * API 항목: area_ar (흡연구역면적)
     */
    private Double areaAr;

    /**
     * API 항목: fclty_knd (시설이미지 URL)
     */
    @Column(length = 1000)
    private String fcltyKnd;

    /**
     * API 항목: inst_nm (관리기관명)
     */
    @Column(length = 100)
    private String instNm;

    /**
     * API 항목: ref_date (데이터기준일자)
     */
    private String refDate;

    @Enumerated(EnumType.STRING)
    private AddressStatus addressStatus;

    /**
     * 엔티티 생성을 위한 빌더.
     * id는 영속성 컨텍스트가 관리하므로 빌더에서 제외한다.
     */
    @Builder
    public SmokingArea(String id, String areaNm, String areaDesc, Coordinate coordinate,
                       Address address, AreaType areaSe, AreaStatus status, Double areaAr,
                       String fcltyKnd, String instNm, String refDate) {
        this.id = id;
        this.areaNm = areaNm;
        this.areaDesc = areaDesc;
        this.coordinate = coordinate;
        this.address = address;
        this.areaSe = areaSe;
        this.status = status;
        this.areaAr = areaAr;
        this.fcltyKnd = fcltyKnd;
        this.instNm = instNm;
        this.refDate = refDate;
    }

    /**
     * 외부 API 동기화 시 기존 데이터의 상태를 갱신하는 비즈니스 메서드.
     * 외부 시스템에 의해 함부로 변경되면 안 되는 식별자(apiId)와
     * 관리자가 수동으로 제어해야 하는 상태값(status, type)은 업데이트 대상에서 제외하여 도메인을 보호한다.
     *
     * @param newArea API를 통해 새로 받아온 최신 흡연구역 정보
     */
    public void update(SmokingArea newArea) {
        this.areaNm = newArea.getAreaNm();
        this.areaDesc = newArea.getAreaDesc();
        this.coordinate = newArea.getCoordinate();
        this.address = newArea.getAddress();
        this.areaAr = newArea.getAreaAr();
        this.instNm = newArea.getInstNm();
        this.fcltyKnd = newArea.getFcltyKnd();
        this.refDate = newArea.getRefDate();
    }

    /**
     * 지오코딩 결과와 생성된 이미지 URL을 바탕으로 위치 정보를 한 번에 보정한다.
     */
    public void compensateLocation(GeocodingResponse res) {
        // 1. 좌표 업데이트
        this.coordinate = new Coordinate(res.latitude(), res.longitude());

        // 2. 주소 정보 업데이트 (Address VO 내부 메서드 활용 권장)
        if (this.address != null) {
            this.address.updateByGeocoding(res);
        }
    }

    // [업데이트 로직] 지오코딩 성공 시 호출
    public void updateGeocodingSuccess(GeocodingResponse res) {
        this.address.updateByGeocoding(res); // Address VO 내부 필드 교체
        this.coordinate = new Coordinate(res.latitude(), res.longitude()); // 좌표 교체
        this.addressStatus = AddressStatus.SUCCESS; // 상태 변경
    }

    // [업데이트 로직] 지오코딩 실패 시 호출
    public void updateGeocodingFail() {
        this.addressStatus = AddressStatus.FAIL;
    }
}