package com.damm.server.modules.area.controller;

import com.damm.server.global.common.ApiResponse;
import com.damm.server.modules.area.service.AdminApiSourceService;
import com.damm.server.modules.area.dto.ApiSourceRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/api-sources")
@RequiredArgsConstructor
public class AdminApiSourceController {

    private final AdminApiSourceService adminApiSourceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> register(@Valid @ModelAttribute ApiSourceRegisterRequest request) {
        adminApiSourceService.register(request);
        return ApiResponse.success();
    }
}