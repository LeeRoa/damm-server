package com.damm.server.modules.area.presentation;

import com.damm.server.global.common.ApiResponse;
import com.damm.server.modules.area.application.AdminApiSourceService;
import com.damm.server.modules.area.dto.ApiSourceRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/api-sources")
@RequiredArgsConstructor
public class AdminApiSourceController {

    private final AdminApiSourceService adminApiSourceService;

    @PostMapping
    public ApiResponse<Void> register(@RequestBody ApiSourceRegisterRequest request) {
        adminApiSourceService.register(request);
        return ApiResponse.success();
    }
}