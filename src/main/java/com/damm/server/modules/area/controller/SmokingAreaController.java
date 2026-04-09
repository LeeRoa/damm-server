package com.damm.server.modules.area.controller;

import com.damm.server.modules.area.dto.NearbySmokingAreaRequest;
import com.damm.server.modules.area.dto.NearbySmokingAreaResponse;
import com.damm.server.modules.area.service.SmokingAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/areas")
@Tag(name = "02. 흡연구역 사용자 API", description = "지도를 기반으로 내 주변 흡연구역을 찾고 정보를 조회합니다.")
public class SmokingAreaController {

    private final SmokingAreaService smokingAreaService;

    @Operation(
            summary = "현 위치 기반 근접 흡연구역 조회",
            description = """
                    사용자의 현재 GPS 좌표를 기준으로 지정된 반경(m) 내에 있는 흡연구역 목록을 거리순으로 반환합니다.\
                    
                    
                    **핵심 로직:** PostGIS의 `ST_DWithin` 인덱스 스캔을 사용하여 대량의 데이터에서도 빠르게 검색을 수행합니다."""
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 (가까운 순서대로 정렬됨)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 좌표값 또는 파라미터 오류"
            )
    })
    @GetMapping("/nearby")
    public List<NearbySmokingAreaResponse> getNearby(
            @Valid @ParameterObject NearbySmokingAreaRequest request
    ) {
        return smokingAreaService.getNearbySmokingAreas(
                request.lat(),
                request.lng(),
                request.getRadiusWithDefault()
        );
    }
}