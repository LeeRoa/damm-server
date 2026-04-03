package com.damm.server.infra.publicdata.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum District {
    // 서울특별시 자식 구역들
    SONGPA(Province.SEOUL, "송파구"),
    SEODAEMUN(Province.SEOUL, "서대문구"),
    JUNGNANG(Province.SEOUL, "중랑구"),
    GWANGJIN(Province.SEOUL, "광진구"),
    EUNPYEONG(Province.SEOUL, "은평구");
    // 필요한 지역은 여기에 계속 추가

    private final Province province;
    private final String koreanName;

    /**
     * DB 컨버터용: 정확히 일치하는 한글 명칭으로 조회한다.
     */
    public static District fromKoreanName(String koreanName) {
        return Arrays.stream(District.values())
                .filter(d -> d.getKoreanName().equals(koreanName))
                .findFirst()
                .orElse(null);
    }

    /**
     * DTO -> Entity 변환용: "송파", "송파구" 모두 매핑한다.
     */
    public static District find(String signgunm) {
        if (signgunm == null || signgunm.isBlank()) {
            return null;
        }

        return Arrays.stream(District.values())
                .filter(d -> {
                    // "송파구"에서 정규식을 이용해 끝에 붙은 '시', '군', '구'를 제거하여 "송파" 추출
                    // "서대문구"는 "서대문"으로 추출됨
                    String coreName = d.koreanName.replaceAll("[시군구]$", "");
                    return signgunm.contains(coreName);
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * 특정 시/도에 속한 구역 목록을 가져오는 유틸리티 메서드
     */
    public static List<District> findByProvince(Province province) {
        return Arrays.stream(District.values())
                .filter(d -> d.getProvince() == province)
                .toList();
    }
}