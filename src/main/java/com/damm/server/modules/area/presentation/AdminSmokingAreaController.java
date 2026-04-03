package com.damm.server.modules.area.presentation;

import com.damm.server.global.common.ApiResponse;
import com.damm.server.modules.area.application.SmokingAreaBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/smoking-areas")
@RequiredArgsConstructor
public class AdminSmokingAreaController {

    private final SmokingAreaBatchService batchService;

    /**
     * 수동으로 공공데이터포털 API를 호출하여 흡연구역 데이터를 DB에 동기화한다.
     */
    @PostMapping("/sync")
    public ApiResponse<Void> syncWithPublicData() {
        batchService.syncAllApiSources();
        return ApiResponse.success();
    }

    @PostMapping("/sync-retry")
    public ApiResponse<String> syncAddressRetry() {
        return ApiResponse.success(batchService.processPendingAddresses() + "건의 주소를 보정했습니다.");
    }
}