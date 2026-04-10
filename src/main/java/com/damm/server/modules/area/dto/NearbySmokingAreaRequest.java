package com.damm.server.modules.area.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "근접 흡연구역 조회 요청 파라미터")
public record NearbySmokingAreaRequest(
        @Schema(description = "현재 위치의 위도 (Latitude)", example = "37.5399")
        @NotNull(message = "위도는 필수값입니다.")
        @Min(value = -90, message = "위도는 -90도 이상이어야 합니다.")
        @Max(value = 90, message = "위도는 90도 이하이어야 합니다.")
        Double lat,

        @Schema(description = "현재 위치의 경도 (Longitude)", example = "127.0834")
        @NotNull(message = "경도는 필수값입니다.")
        @Min(value = -180, message = "경도는 -180도 이상이어야 합니다.")
        @Max(value = 180, message = "경도는 180도 이하이어야 합니다.")
        Double lng,

        @Schema(description = "검색 반경 (단위: 미터, 최대 5km)", example = "1000")
        @Min(value = 1, message = "반경은 최소 1m 이상이어야 합니다.")
        @Max(value = 5000, message = "반경은 최대 5000m(5km)를 넘을 수 없습니다.")
        Double radius,

        String type,
        String status
) {}