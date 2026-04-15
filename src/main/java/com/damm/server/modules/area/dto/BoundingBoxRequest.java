package com.damm.server.modules.area.dto;

import jakarta.validation.constraints.NotNull;

public record BoundingBoxRequest(
        @NotNull Double swLat, // 남서쪽 위도 (South-West)
        @NotNull Double swLng, // 남서쪽 경도
        @NotNull Double neLat, // 북동쪽 위도 (North-East)
        @NotNull Double neLng  // 북동쪽 경도
) {}