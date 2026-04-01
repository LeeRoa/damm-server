package com.damm.server.modules.area.domain.enums;

public enum AreaType {
    INDOOR,     // 실내
    OUTDOOR,    // 실외 부스
    OPEN,       // 개방형
    GENERAL     // 일반 (세부 구분 없음)
    ;

    public static AreaType from(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return GENERAL;
        }

        // 1. 실내 키워드 우선 확인
        if (rawValue.contains("실내")) {
            return INDOOR;
        }

        // 2. 실외 또는 부스 키워드 확인
        if (rawValue.contains("실외") || rawValue.contains("부스")) {
            return OUTDOOR;
        }

        // 3. 개방형 키워드 확인
        if (rawValue.contains("개방")) {
            return OPEN;
        }

        // 4. 그 외에는 일반형으로 분류
        return GENERAL;
    }
}