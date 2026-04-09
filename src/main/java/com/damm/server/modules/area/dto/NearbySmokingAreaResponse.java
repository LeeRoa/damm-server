package com.damm.server.modules.area.dto;

import lombok.Builder;

@Builder
public record NearbySmokingAreaResponse(
        Long internalId,
        String id,              // 식별자
        String name,            // 흡연구역 명칭
        String description,     // 상세 위치 설명
        String address,         // 주소
        String type,            // 구역 구분 (개방형/폐쇄형)
        Double latitude,        // 위도
        Double longitude,       // 경도
        Integer distanceMeter,  // 거리 (반올림된 정수)
        String imageUrl,        // 시설 이미지 (fclty_knd)
        String status           // 운영 상태
) {
    public static NearbySmokingAreaResponse from(SmokingAreaDistanceProjection projection) {
        return NearbySmokingAreaResponse.builder()
                .internalId(projection.getInternalId())
                .id(projection.getId())
                .name(projection.getAreaNm())
                .description(projection.getAreaDesc())
                .address(projection.getRawAddress())
                .type(projection.getAreaSe())
                .latitude(projection.getLatitude())
                .longitude(projection.getLongitude())
                .distanceMeter((int) Math.round(projection.getDistance())) // 거리 반올림
                .imageUrl(projection.getFcltyKnd())
                .status(projection.getStatus())
                .build();
    }
}