package com.damm.server.modules.area.controller;

import com.damm.server.global.common.ApiResponse;
import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.dto.AdminAreaApprovalRequest;
import com.damm.server.modules.area.service.AdminSmokingAreaService;
import com.damm.server.modules.area.service.SmokingAreaBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/smoking-areas")
@RequiredArgsConstructor
@Tag(name = "01. 어드민 - 흡연구역 데이터 관리", description = "공공데이터 동기화 및 사용자 제보 승인, 주소 보정 배치 작업을 제어합니다.")
public class AdminSmokingAreaController {

    private final SmokingAreaBatchService batchService;
    private final AdminSmokingAreaService adminSmokingAreaService; // [추가] 어드민 승인 서비스 주입

    // ===================================================================================
    // 1. 공공데이터 배치 관리 로직
    // ===================================================================================

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

    // ===================================================================================
    // 2. 사용자 제보 승인 관리 로직
    // ===================================================================================

    @Operation(
            summary = "승인 대기(PENDING) 제보 목록 조회",
            description = "사용자가 앱을 통해 제보하여 관리자 검토를 기다리고 있는 PENDING 상태의 흡연구역 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공 (대기 목록 반환)")
    })
    @GetMapping("/pending")
    public ApiResponse<List<SmokingArea>> getPendingAreas() {
        return ApiResponse.success(adminSmokingAreaService.getPendingAreas());
    }

    @Operation(
            summary = "사용자 제보 수정 및 승인/반려 처리",
            description = """
                    관리자가 대기 중인 제보 데이터를 검토하고 내용(이름, 주소, 타입 등)을 수정한 뒤 상태를 변경합니다.
                    상태를 `VERIFIED`로 변경할 경우, 즉시 일반 사용자의 지도 화면에 해당 핀이 노출됩니다."""
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "승인/반려 처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 제보 ID")
    })
    @PatchMapping("/{id}/approve")
    public ApiResponse<Void> processApproval(
            @PathVariable Long id,
            @Valid @RequestBody AdminAreaApprovalRequest request) {

        adminSmokingAreaService.processApproval(id, request);
        return ApiResponse.success(); // 내부에서 return null 대신 빈 응답 처리
    }
}