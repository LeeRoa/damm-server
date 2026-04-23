package com.damm.server.infra.storage.domain;

import com.damm.server.infra.storage.validator.FileCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageDomain {
    // 도메인별 폴더명과 허용 파일 종류 정의
    AREA(FileCategory.IMAGE),
    PROFILE(FileCategory.IMAGE),
    REVIEW(FileCategory.IMAGE),
    COMMON(FileCategory.IMAGE);

    private final FileCategory fileCategory;
}