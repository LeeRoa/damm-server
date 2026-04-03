package com.damm.server.infra.publicdata.dto;

import java.util.Map;

public record SmokingAreaItem(Map<String, String> data) {
    // 공통 상수 정의
    public static final String KEY_ID = "id";
    public static final String 흡연구역_명칭 = "areaNm";
    public static final String 설치_위치_상세 = "areaDesc";
    public static final String 시도_명칭 = "ctprvnnm";
    public static final String 시군구_명칭 = "signgunm";
    public static final String 읍면동_명칭 = "emdnm";
    public static final String 흡연구역_구분 = "areaSe";
    public static final String 면적 = "areaAr";
    public static final String 도로명_주소 = "rdnmadr";
    public static final String 지번_주소 = "lnmadr";
    public static final String 관리_기관_명칭 = "instNm";
    public static final String 위도 = "latitude";
    public static final String 경도 = "longitude";
    public static final String 시설_구분 = "fcltyKnd";
    public static final String 데이터_기준_일자 = "refDate";

    public String get(String key) {
        return data.get(key);
    }

    public String getOrDefault(String key, String defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    public String getAssembledAddress() {
        // 1. 지역 접두사 생성 (예: "서울특별시 중랑구")
        String province = getOrDefault(시도_명칭, "").trim();
        String district = getOrDefault(시군구_명칭, "").trim();

        StringBuilder prefixBuilder = new StringBuilder();
        if (!province.isEmpty()) prefixBuilder.append(province);
        if (!district.isEmpty()) {
            if (!prefixBuilder.isEmpty()) prefixBuilder.append(" ");
            prefixBuilder.append(district);
        }
        String prefix = prefixBuilder.toString();

        // 2. 우선순위에 따른 상세 주소 결정 (도로명 -> 지번 -> 읍면동/상세)
        String detailedAddress = selectDetailedAddress();

        // 3. 접두사와 상세 주소 스마트 결합
        return combineSmartly(prefix, detailedAddress);
    }

    /**
     * 우선순위 전략에 따라 가장 신뢰도 높은 상세 주소를 선택한다.
     */
    private String selectDetailedAddress() {
        // 1순위: 도로명 주소
        String rdnm = getOrDefault(도로명_주소, "").trim();
        if (!rdnm.isEmpty()) return rdnm;

        // 2순위: 지번 주소
        String lnm = getOrDefault(지번_주소, "").trim();
        if (!lnm.isEmpty()) return lnm;

        // 3순위: 조각난 정보 합치기 (읍면동 + 상세 지명)
        String emd = getOrDefault(읍면동_명칭, "").trim();
        String desc = getOrDefault(설치_위치_상세, "").trim();
        return (emd + " " + desc).trim();
    }

    /**
     * 접두사(시도/시군구)가 상세 주소에 이미 포함되어 있는지 확인 후 결합한다.
     */
    private String combineSmartly(String prefix, String detailed) {
        if (detailed.isEmpty()) return prefix;
        if (prefix.isEmpty()) return detailed;

        // 상세 주소가 접두사로 시작하거나 접두사를 포함하고 있다면 상세 주소만 반환
        // 예: prefix="서울특별시 중랑구", detailed="서울특별시 중랑구 용마산로..."
        if (detailed.contains(prefix) || detailed.startsWith(prefix.split(" ")[0])) {
            return detailed;
        }

        // 포함되어 있지 않다면 접두사를 붙여서 반환
        return prefix + " " + detailed;
    }
}