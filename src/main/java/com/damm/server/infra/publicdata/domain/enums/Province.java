package com.damm.server.infra.publicdata.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Province {
    SEOUL("서울특별시"),
    BUSAN("부산광역시"),
    DAEGU("대구광역시"),
    INCHEON("인천광역시"),
    GWANGJU("광주광역시"),
    DAEJEON("대전광역시"),
    ULSAN("울산광역시"),
    SEJONG("세종특별자치시"),
    GYEONGGI("경기도"),
    GANGWON("강원특별자치도"),
    CHUNGBUK("충청북도"),
    CHUNGNAM("충청남도"),
    JEONBUK("전북특별자치도"),
    JEONNAM("전라남도"),
    GYEONGBUK("경상북도"),
    GYEONGNAM("경상남도"),
    JEJU("제주특별자치도");

    private final String koreanName;

    /**
     * DB 컨버터용: 정확히 일치하는 한글 명칭으로 조회한다.
     */
    public static Province fromKoreanName(String koreanName) {
        return Arrays.stream(Province.values())
                .filter(p -> p.getKoreanName().equals(koreanName))
                .findFirst()
                .orElse(null); // 에러보다는 null 처리가 안전할 수 있다.
    }

    /**
     * DTO -> Entity 변환용: "서울", "서울시", "서울특별시" 모두 SEOUL로 매핑한다.
     */
    public static Province find(String ctprvnnm) {
        if (ctprvnnm == null || ctprvnnm.isBlank()) {
            return null;
        }

        return Arrays.stream(Province.values())
                .filter(p -> {
                    // 1. "서울특별시"가 "서울"을 포함하거나 (서울시, 서울특별시 대응)
                    // 2. 입력값이 "서울"이라는 핵심 키워드를 포함할 때
                    String coreName = p.koreanName.substring(0, 2);
                    return ctprvnnm.contains(coreName);
                })
                .findFirst()
                .orElse(null); // 매칭되는 게 없으면 null 반환 후 로그 처리가 깔끔하다.
    }
}