package com.damm.server.infra.publicdata.dto;

import com.damm.server.infra.publicdata.domain.enums.Province;
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

    public Province toProvince() {
        return Province.find(get(KEY_CTPRVNNM));
    }
}