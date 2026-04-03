package com.damm.server.infra.publicdata.dto;

import com.damm.server.infra.publicdata.domain.enums.Province;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SmokingAreaItem(
        // 흡연구역 아이디
        @JsonProperty("id") String id,

        // 흡연구역 명
        @JsonProperty("area_nm") String areaNm,

        // 흡연구역 범위 상세
        @JsonProperty("area_desc") String areaDesc,

        // 시도명
        @JsonProperty("ctprvnnm") String ctprvnnm,

        // 시군구명
        @JsonProperty("signgunm") String signgunm,

        // 읍면동명
        @JsonProperty("emdnm") String emdnm,

        // 흡연구역 구분
        @JsonProperty("area_se") String areaSe,

        // 흡연구역 면적
        @JsonProperty("area_ar") String areaAr,

        // 소재지 도로명 주소
        @JsonProperty("rdnmadr") String rdnmadr,

        // 소재지 지번 주소
        @JsonProperty("lnmadr") String lnmadr,

        // 관리기관 명
        @JsonProperty("inst_nm") String instNm,

        // 위도
        @JsonProperty("latitude") String latitude,

        // 경도
        @JsonProperty("longitude") String longitude,

        // 시설 이미지
        @JsonProperty("fclty_knd") String fcltyKnd,

        // 데이터 기준일자
        @JsonProperty("ref_date") String refDate
) {
    public Province toProvince() {
        return Province.find(this.ctprvnnm);
    }
}