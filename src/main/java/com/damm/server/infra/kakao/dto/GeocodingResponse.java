package com.damm.server.infra.kakao.dto;

import com.damm.server.infra.publicdata.domain.enums.District;
import com.damm.server.infra.publicdata.domain.enums.Province;

public record GeocodingResponse(
        Double latitude,
        Double longitude,
        String rdnmadr,    // 보정된 도로명 주소
        String lnmadr,     // 보정된 지번 주소
        Province province, // 추출된 시/도
        District district, // 추출된 시/군/구
        String emdnm      // 추출된 읍/면/동
) {}