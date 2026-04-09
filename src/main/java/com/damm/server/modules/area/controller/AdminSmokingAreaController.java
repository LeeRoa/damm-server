package com.damm.server.modules.area.controller;

import com.damm.server.global.common.ApiResponse;
import com.damm.server.modules.area.service.SmokingAreaBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/smoking-areas")
@RequiredArgsConstructor
@Tag(name = "01. 어드민 - 흡연구역 데이터 관리", description = "공공데이터 동기화 및 주소 보정 배치 작업을 제어합니다.")
public class AdminSmokingAreaController {

    private final SmokingAreaBatchService batchService;

    @Operation(
            summary = "전체 공공데이터 동기화 실행",
            description = "등록된 모든 API 소스에서 데이터를 수집하여 DB를 최신화합니다. " +
                    "신규 데이터는 저장하고, 기존 데이터는 업데이트하며, 위경도 좌표를 기반으로 PostGIS 공간 데이터(location)를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "동기화 작업 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "외부 API 통신 오류 또는 DB 처리 실패")
    })
    @PostMapping("/sync")
    public ApiResponse<Void> syncWithPublicData() {
        batchService.syncAllApiSources();
        return ApiResponse.success();
    }

    @Operation(
            summary = "실패 주소(Geocoding) 재처리",
            description = "주소 상태가 'FAIL'이거나 누락된 데이터를 추출하여 카카오 로컬 API를 통해 위경도 좌표를 다시 획득합니다. " +
                    "보정 성공 시 해당 데이터의 location 컬럼도 자동으로 업데이트됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재처리 완료 (성공 건수 반환)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "카카오 API 쿼터 초과")
    })
    @PostMapping("/sync-retry")
    public ApiResponse<String> syncAddressRetry() {
        return ApiResponse.success(batchService.processPendingAddresses() + "건의 주소를 보정했습니다.");
    }
}