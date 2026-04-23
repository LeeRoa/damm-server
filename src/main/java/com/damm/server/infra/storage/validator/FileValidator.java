package com.damm.server.infra.storage.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileValidator {

    private final Tika tika;

    public void validate(MultipartFile file, FileCategory category) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String mimeType = tika.detect(inputStream);

            if (!mimeType.startsWith(category.getMimePrefix())) {
                throw new IllegalArgumentException(category.name() + " 형식의 파일만 업로드 가능합니다.");
            }
            if (file.getSize() > category.getMaxSizeBytes()) {
                throw new IllegalArgumentException(category.name() + " 파일은 " + (category.getMaxSizeBytes() / 1024 / 1024) + "MB를 초과할 수 없습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 검증 중 에러 발생");
        }
    }
}