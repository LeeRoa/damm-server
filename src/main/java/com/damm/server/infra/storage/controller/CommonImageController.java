package com.damm.server.infra.storage.controller;

import com.damm.server.global.common.ApiResponse;
import com.damm.server.infra.storage.ImageStorageService;
import com.damm.server.infra.storage.domain.StorageDomain;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common/images")
@Tag(name = "00. 공통 - 이미지 업로드", description = "이미지 파일을 서버에 업로드하고 접근 가능한 URL을 획득합니다.")
public class CommonImageController {

    private final ImageStorageService imageStorageService;

    @Operation(
            summary = "단일 이미지 업로드",
            description = "MultipartFile 형태의 이미지를 업로드합니다. 성공 시 접근 가능한 상대 경로 URL을 반환합니다."
    )
    @PostMapping(value = "/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadImage(
            @PathVariable StorageDomain type,
            @RequestPart("file") MultipartFile file) {
        return ApiResponse.success(imageStorageService.upload(file, type));
    }
}