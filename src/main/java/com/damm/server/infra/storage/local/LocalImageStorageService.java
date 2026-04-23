package com.damm.server.infra.storage.local;

import com.damm.server.infra.storage.ImageStorageService;
import com.damm.server.infra.storage.domain.StorageDomain;
import com.damm.server.infra.storage.validator.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class LocalImageStorageService implements ImageStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir; // application.yml에서 주입받을 경로

    @Value("${file.access-path}")
    private String accessPath;

    private final FileValidator fileValidator;

    @Override
    public String upload(MultipartFile file, StorageDomain domain) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        try {
            Path uploadPath = Paths.get(uploadDir, domain.name()).toAbsolutePath().normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            fileValidator.validate(file, domain.getFileCategory());

            // 파일명 생성
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String savedFilename = UUID.randomUUID() + extension;

            // 파일 저장
            Path targetLocation = uploadPath.resolve(savedFilename);
            file.transferTo(targetLocation.toFile());

            // 반환 예시 URL: /uploads/AREA/uuid.jpg
            return accessPath + domain.name() + "/" + savedFilename;

        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }
    }
}