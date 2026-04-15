package com.damm.server.modules.area.controller;

import com.damm.server.global.common.ApiResponse;
import com.damm.server.modules.area.dto.*;
import com.damm.server.modules.area.service.SmokingAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<List<SmokingAreaSearchResponse>> getNearby(
            @Valid @ParameterObject NearbySmokingAreaRequest request
    ) {
        return ApiResponse.success(smokingAreaService.getNearbyAreas(request));
    }

    @Operation(summary = "통합 키워드 검색", description = "이름이나 주소로 흡연구역을 검색합니다. (전국 대상)")
    @GetMapping("/search")
    public ApiResponse<List<SmokingAreaSearchResponse>> search(
            @Valid @ParameterObject SmokingAreaSearchRequest request
    ) {
        return ApiResponse.success(smokingAreaService.searchAreas(request));
    }

    @Operation(
            summary = "지도 화면 영역(Bounding Box) 기반 핀 조회",
            description = """
                    클라이언트의 지도 화면에 렌더링할 핀 데이터를 조회합니다.
                    지도를 드래그할 때마다 변경되는 화면의 좌하단(남서쪽)과 우상단(북동쪽) 좌표를 입력받아 해당 네모 영역 내의 데이터만 가볍게 반환합니다.\
                    
                    
                    **핵심 로직:** PostGIS의 `&&` 연산자와 `ST_MakeEnvelope`를 활용하여 GiST 공간 인덱스를 100% 활용하는 빠른 검색을 수행합니다."""
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 (화면 내 핀 목록 반환)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "바운딩 박스 좌표 누락 또는 형식 오류"
            )
    })
    @GetMapping("/map")
    public ApiResponse<List<SmokingAreaPinResponse>> getMapPins(
            @Valid @ParameterObject @ModelAttribute BoundingBoxRequest request) {
        List<SmokingAreaPinResponse> pins = smokingAreaService.getMapPins(request);
        return ApiResponse.success(pins);
    }

    @Operation(
            summary = "신규 흡연구역 제보",
            description = """
                    사용자가 발견한 새로운 흡연구역의 위치와 상세 정보를 제보합니다.
                    제보된 데이터는 어뷰징 방지를 위해 즉시 지도에 노출되지 않으며, 관리자의 승인(VERIFIED) 전까지 대기(PENDING) 상태로 저장됩니다.\
                    
                    
                    **핵심 로직:** JPA 영속성 컨텍스트를 활용하여 제보 데이터와 JTS Point(공간 데이터)를 매핑하고 안전하게 DB에 적재합니다."""
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "제보 성공 (생성된 내부 ID 반환)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "필수 입력값 누락 또는 위경도 범위 초과"
            )
    })
    @PostMapping("/suggest")
    public ApiResponse<Long> suggestArea(@Valid @RequestBody SmokingAreaSuggestRequest request) {
        return ApiResponse.success(smokingAreaService.suggestNewArea(request));
    }
}