package com.damm.server.modules.area.dto;

import com.damm.server.modules.area.domain.enums.AreaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SmokingAreaSuggestRequest(
        @NotBlank(message = "장소 이름을 입력해주세요.")
        String name,

        @NotBlank(message = "주소를 확인해주세요.")
        String address,

        @Schema(description = "위치 상세 설명", example = "건물 뒤편 주차장 입구")
        String description,

        @NotNull @Min(-90) @Max(90)
        Double latitude,

        @NotNull @Min(-180) @Max(180)
        Double longitude,

        @Schema(description = "구역 구분 (GENERAL, OPEN 등)", example = "GENERAL")
        AreaType type,

        @Schema(description = "제보 사진 URL")
        String imageUrl
) {
}