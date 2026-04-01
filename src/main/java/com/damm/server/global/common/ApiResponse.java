package com.damm.server.global.common;

import com.damm.server.global.error.ErrorCode;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    private final boolean success;    // 성공 여부
    private final T data;             // 실제 데이터 (SmokingArea 리스트 등)
    private final String message;     // 사용자 알림 메시지
    private final String errorCode;   // 내부 비즈니스 에러 코드
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, T data, String message, String errorCode) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    // 성공 응답 (데이터가 있는 경우)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "요청이 성공적으로 처리되었습니다.", null);
    }

    // 성공 응답 (데이터가 없는 경우 - 삭제, 수정 등)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, "요청이 성공적으로 처리되었습니다.", null);
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.getMessage(), errorCode.getCode());
    }
}