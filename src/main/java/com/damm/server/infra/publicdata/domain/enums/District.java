package com.damm.server.infra.publicdata.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum District {
    // 서울특별시
    JONGNO(Province.SEOUL, "종로구"),
    JUNG(Province.SEOUL, "중구"),
    YONGSAN(Province.SEOUL, "용산구"),
    SEONGDONG(Province.SEOUL, "성동구"),
    GWANGJIN(Province.SEOUL, "광진구"),
    DONGDAEMUN(Province.SEOUL, "동대문구"),
    JUNGNANG(Province.SEOUL, "중랑구"),
    SEONGBUK(Province.SEOUL, "성북구"),
    GANGBUK(Province.SEOUL, "강북구"),
    DOBONG(Province.SEOUL, "도봉구"),
    NOWON(Province.SEOUL, "노원구"),
    EUNPYEONG(Province.SEOUL, "은평구"),
    SEODAEMUN(Province.SEOUL, "서대문구"),
    MAPO(Province.SEOUL, "마포구"),
    YANGCHEON(Province.SEOUL, "양천구"),
    GANGSEO(Province.SEOUL, "강서구"),
    GURO(Province.SEOUL, "구로구"),
    GEUMCHEON(Province.SEOUL, "금천구"),
    YEONGDEUNGPO(Province.SEOUL, "영등포구"),
    DONGJAK(Province.SEOUL, "동작구"),
    GWANAK(Province.SEOUL, "관악구"),
    SEOCHO(Province.SEOUL, "서초구"),
    GANGNAM(Province.SEOUL, "강남구"),
    SONGPA(Province.SEOUL, "송파구"),
    GANGDONG(Province.SEOUL, "강동구");

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