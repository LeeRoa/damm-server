package com.damm.server.global.util;

import java.util.ArrayList;
import java.util.List;

public class AddressUtil {

    /**
     * 주소 문자열을 지오코딩 검색에 최적화된 형태로 정제한다.
     */
    public static String refineForGeocoding(String rawAddress) {
        if (rawAddress == null || rawAddress.isBlank()) return "";

        // 1. 1차 정제: 정규식을 이용한 노이즈 제거
        String cleaned = rawAddress.replaceAll("\\(.*?\\)", " "); // 괄호는 공백으로 치환해서 단어 유실 방지

        if (cleaned.contains(",")) {
            cleaned = cleaned.split(",")[0];
        }

        // 숫자 뒤 '번지', '외 N필지', '층/지상/지하' 패턴 제거
        cleaned = cleaned.replaceAll("(?<=\\d)번지", "");
        cleaned = cleaned.replaceAll("외\\s*\\d+\\s*필지", "");
        cleaned = cleaned.replaceAll("(지상|지하)?\\s*\\d+\\s*층", "");
        cleaned = cleaned.replaceAll("(지상|지하)$", "");

        // 숫자(지번/건물번호) 뒤에 붙은 한글 부가 명칭 제거 (예: 123-1 성북빌딩 -> 123-1)
        cleaned = cleaned.replaceAll("(\\d+(?:-\\d+)?)\\s+[가-힣].*", "$1");

        // 2. 2차 정제: 토큰 단위 중복 제거 알고리즘
        String[] tokens = cleaned.trim().split("\\s+");
        List<String> refinedTokens = new ArrayList<>();

        for (String token : tokens) {
            if (token.isBlank()) continue;

            boolean isRedundant = false;
            for (int i = 0; i < refinedTokens.size(); i++) {
                String existing = refinedTokens.get(i);

                // 중복 및 포함 관계 검사 (예: 서울 vs 서울특별시, 성북 vs 성북구)
                if (existing.contains(token)) {
                    // 기존 단어가 새 단어를 포함하면 새 단어는 무시 (서울 < 서울특별시)
                    isRedundant = true;
                    break;
                }

                if (token.contains(existing)) {
                    // 새 단어가 기존 단어를 포함하면 기존 단어를 교체 (서울 -> 서울특별시)
                    refinedTokens.set(i, token);
                    isRedundant = true;
                    break;
                }
            }

            if (!isRedundant) {
                refinedTokens.add(token);
            }
        }

        // 3. 최종 결합 및 공백 정리
        return String.join(" ", refinedTokens).trim();
    }
}