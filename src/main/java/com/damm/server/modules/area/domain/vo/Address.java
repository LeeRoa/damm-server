package com.damm.server.modules.area.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(length = 10)
    private String zipCode;       // 우편번호 (예: 05006)

    @Column(length = 200)
    private String roadAddress;   // 도로명 주소 (예: 서울특별시 광진구 능동로 209)

    @Column(length = 200)
    private String lotAddress;    // 지번 주소 (예: 서울특별시 광진구 군자동 98)

    @Column(length = 100)
    private String detailAddress; // 상세 주소 (예: 학생회관 1층 옥상)

    // DB 검색 및 필터링(예: "광진구"의 흡연실만 검색)을 위해 시/도, 시/군/구는 별도 컬럼으로 유지.
    @Column(length = 20)
    private String sido;          // 시/도 (예: 서울특별시)

    @Column(length = 20)
    private String sigungu;       // 시/군/구 (예: 광진구)

    @Builder
    public Address(String zipCode, String roadAddress, String lotAddress,
                   String detailAddress, String sido, String sigungu) {
        this.zipCode = zipCode;
        this.roadAddress = roadAddress;
        this.lotAddress = lotAddress;
        this.detailAddress = detailAddress;
        this.sido = sido;
        this.sigungu = sigungu;
    }
}