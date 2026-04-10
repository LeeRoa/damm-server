package com.damm.server.modules.area.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SmokingAreaSearchRequest(
        @Schema(description = "검색 키워드 (이름 또는 주소)", example = "강남역")
        @NotBlank(message = "검색어는 한 글자 이상 입력해주세요.")
        String keyword,

        @Schema(description = "현재 위도 (거리 계산용)", example = "37.5399")
        Double lat,

        @Schema(description = "현재 경도 (거리 계산용)", example = "127.0834")
        Double lng,

        String type,
        String status
) {
}