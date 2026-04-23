package com.damm.server.infra.storage;

import com.damm.server.infra.storage.domain.ImageType;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    /**
     * 이미지를 저장하고, 외부에서 접근 가능한 URL을 반환한다.
     */
    String upload(MultipartFile file, ImageType imageType);
}