package com.damm.server.infra.kakao.dto;

public record GeocodingResponse(
        Double latitude,
        Double longitude,
        String lnmadr,    // 지번 주소
        String emdnm      // 읍면동명
) {}