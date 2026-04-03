package com.damm.server.infra.publicdata.dto;

import java.util.Map;

public record SmokingAreaItem(Map<String, String> data) {
    // 공통 상수 정의
    public static final String KEY_ID = "id";
    public static final String KEY_AREA_NM = "areaNm";
    public static final String KEY_AREA_DESC = "areaDesc";
    public static final String KEY_CTPRVNNM = "ctprvnnm";
    public static final String KEY_SIGNGUNM = "signgunm";
    public static final String KEY_EMDNM = "emdnm";
    public static final String KEY_AREA_SE = "areaSe";
    public static final String KEY_AREA_AR = "areaAr";
    public static final String KEY_RDNMADR = "rdnmadr";
    public static final String KEY_LNMADR = "lnmadr";
    public static final String KEY_INST_NM = "instNm";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_FCLTY_KND = "fcltyKnd";
    public static final String KEY_REF_DATE = "refDate";

    public String get(String key) {
        return data.get(key);
    }

    public String getOrDefault(String key, String defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    public String getAssembledAddress() {
        // 1. 지역 접두사 생성 (예: "서울특별시 중랑구")
        String province = getOrDefault(KEY_CTPRVNNM, "").trim();
        String district = getOrDefault(KEY_SIGNGUNM, "").trim();

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
        String rdnm = getOrDefault(KEY_RDNMADR, "").trim();
        if (!rdnm.isEmpty()) return rdnm;

        // 2순위: 지번 주소
        String lnm = getOrDefault(KEY_LNMADR, "").trim();
        if (!lnm.isEmpty()) return lnm;

        // 3순위: 조각난 정보 합치기 (읍면동 + 상세 지명)
        String emd = getOrDefault(KEY_EMDNM, "").trim();
        String desc = getOrDefault(KEY_AREA_DESC, "").trim();
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