package com.damm.server.global.error;

import com.damm.server.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 또는 @Validated 검증 실패 시 (Query Parameter, ModelAttribute)
     * NearbySmokingAreaRequest 처리 시 발생
     */
    @ExceptionHandler(BindException.class)
    protected ApiResponse<Void> handleBindException(BindException e) {
        String errorMessage = getErrorMessage(e.getBindingResult());
        log.warn("Validation failed (BindException): {}", errorMessage);
        return ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
    }

    /**
     * @Valid 또는 @Validated 검증 실패 시 (RequestBody JSON)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = getErrorMessage(e.getBindingResult());
        log.warn("Validation failed (MethodArgumentNotValidException): {}", errorMessage);
        return ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
    }

    /**
     * 비즈니스 예외 처리 (DammException)
     */
    @ExceptionHandler(DammException.class)
    protected ApiResponse<Void> handleDammException(DammException e) {
        log.error("Damm Exception: {}", e.getErrorCode().getMessage());
        return ApiResponse.error(e.getErrorCode());
    }

    /**
     * 그 외 예상치 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ApiResponse<Void> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * BindingResult에서 발생한 모든 필드 에러 메시지를 하나로 합쳐줍니다.
     */
    private String getErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> String.format("[%s: %s]", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
    }
}