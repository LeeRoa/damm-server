package com.damm.server.infra.storage.validator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileCategory {
    IMAGE("image/", 5 * 1024 * 1024), // 이미지는 5MB 제한
    VIDEO("video/", 50 * 1024 * 1024), // 비디오는 50MB 제한
    PDF("application/pdf", 10 * 1024 * 1024); // PDF는 10MB 제한

    private final String mimePrefix;
    private final long maxSizeBytes;
}