package com.damm.server.modules.area.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근접 흡연구역 조회 응답 정보")
public record NearbySmokingAreaResponse(
        @Schema(description = "시스템 내부 고유 PK", example = "268")
        Long internalId,

        @Schema(description = "공공데이터 관리 식별자", example = "구의1동-02-01-042")
        String id,

        @Schema(description = "흡연구역 명칭", example = "신도브래뉴오피스텔 후면")
        String name,

        @Schema(description = "위치 상세 설명 (예: 건물 뒤편, 주차장 옆 등)", example = "오피스텔 후면")
        String description,

        @Schema(description = "도로명 또는 지번 주소", example = "서울특별시 광진구 자양로 138")
        String address,

        @Schema(description = "구역 구분 (예: 실외개방형, 실내흡연실 등)", example = "GENERAL")
        String type,

        @Schema(description = "위도 (WGS84 좌표계)", example = "37.539989")
        Double latitude,

        @Schema(description = "경도 (WGS84 좌표계)", example = "127.083494")
        Double longitude,

        @Schema(description = "사용자 현재 위치로부터의 거리 (단위: 미터)", example = "44")
        Integer distanceMeter,

        @Schema(description = "시설 이미지 URL", example = "http://.../images/sample.jpeg")
        String imageUrl,

        @Schema(description = "운영 상태 (VERIFIED: 검증됨, CLOSED: 폐쇄 등)", example = "VERIFIED")
        String status
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
                .distanceMeter((int) Math.round(projection.getDistance()))
                .imageUrl(projection.getFcltyKnd())
                .status(projection.getStatus())
                .build();
    }
}