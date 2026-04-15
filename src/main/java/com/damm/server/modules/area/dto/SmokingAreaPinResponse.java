package com.damm.server.modules.area.dto;

import com.damm.server.modules.area.domain.enums.AreaType;

public record SmokingAreaPinResponse(
        String id,          // API ID (또는 internalId)
        Double latitude,
        Double longitude,
        AreaType areaType       // 구역 타입 (아이콘 모양을 다르게 그리기 위함)
) {}