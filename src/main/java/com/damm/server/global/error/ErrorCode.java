package com.damm.server.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 1. Common (G)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G002", "적절하지 않은 입력값입니다."),

    // 2. File/CSV (F)
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "F001", "업로드한 파일이 비어있습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "F002", "지원하지 않는 파일 형식입니다. CSV 파일을 업로드해주세요."),
    CSV_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F003", "CSV 파일 파싱 중 오류가 발생했습니다."),

    // 3. Smoking Area (A)
    AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "해당 흡연구역을 찾을 수 없습니다."),
    DUPLICATE_AREA(HttpStatus.CONFLICT, "A002", "이미 등록된 흡연구역입니다."),

    // 4. External API (E)
    KAKAO_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E001", "카카오 지도 API 호출에 실패했습니다."),
    GEOCODING_FAILED(HttpStatus.NOT_FOUND, "E002", "주소를 좌표로 변환하는 데 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}