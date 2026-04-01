package com.damm.server.global.error;

import com.damm.server.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 우리가 정의한 비즈니스 예외 처리
    @ExceptionHandler(DammException.class)
    protected ApiResponse<Void> handleDammException(DammException e) {
        log.error("DammException: {}", e.getErrorCode().getMessage());
        return ApiResponse.error(e.getErrorCode());
    }

    // 그 외 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    protected ApiResponse<Void> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}