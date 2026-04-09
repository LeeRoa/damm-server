package com.damm.server.modules.area.controller;

import com.damm.server.global.common.ApiResponse;
import com.damm.server.modules.area.service.AdminApiSourceService;
import com.damm.server.modules.area.dto.ApiSourceRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/api-sources")
@Tag(name = "00. 어드민 - 데이터 소스 관리", description = "공공데이터 API 출처(Endpoint, 지역 타입 등)를 등록하고 관리하는 API입니다.")
public class AdminApiSourceController {

    private final AdminApiSourceService adminApiSourceService;

    @Operation(
            summary = "신규 API 소스 등록",
            description = "데이터 동기화의 대상이 되는 새로운 외부 API 출처 정보를 등록합니다. 멀티파트 폼 데이터 형식을 사용합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "등록 성공"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> register(@Valid @ModelAttribute ApiSourceRegisterRequest request) {
        adminApiSourceService.register(request);
        return ApiResponse.success();
    }
}