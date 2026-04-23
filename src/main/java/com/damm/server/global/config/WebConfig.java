package com.damm.server.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.access-path}")
    private String accessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // application.yml에 설정한 경로를 절대 경로로 변환
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String absolutePath = uploadPath.toString();

        // 경로 끝에 '/'가 없으면 붙여줌 (매핑 규칙)
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }

        String handlerPattern = accessPath;
        if (!handlerPattern.endsWith("/")) {
            handlerPattern += "/";
        }

        handlerPattern += "**";

        // 3. 매핑 등록
        registry.addResourceHandler(handlerPattern)
                .addResourceLocations("file:" + absolutePath);

        log.debug("정적 리소스 매핑 완료: /uploads/** -> file:{}", absolutePath);
    }
}