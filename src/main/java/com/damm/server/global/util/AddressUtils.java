package com.damm.server.global.util;

public class AddressUtils {

    /**
     * 주소 문자열을 지오코딩 검색에 최적화된 형태로 정제한다.
     */
    public static String refineForGeocoding(String rawAddress) {
        if (rawAddress == null || rawAddress.isBlank()) return "";

        String refined = rawAddress;

        // 1. 괄호 내용 삭제 (예: (묵동), (신내동) 등 행정동 정보)
        refined = refined.replaceAll("\\(.*?\\)", "");

        // 2. 콤마(,) 기준 절삭 (상세 건물명, 호수 등 제거)
        if (refined.contains(",")) {
            refined = refined.split(",")[0];
        }

        // 3. '번지' 단어 삭제 (숫자 뒤의 '번지'는 노이즈인 경우가 많음)
        refined = refined.replaceAll("(?<=\\d)번지", "");

        // 4. '외 N필지' 패턴 삭제 (외 1필지, 외1필지 등)
        refined = refined.replaceAll("외\\s*\\d+\\s*필지", "");

        // 5. 층수 및 지상/지하 정보 삭제
        // 지상2층, 2층, 지하1층 등 처리
        refined = refined.replaceAll("(지상|지하)?\\s*\\d+\\s*층", "");
        refined = refined.replaceAll("(지상|지하)$", ""); // 숫자 없이 남은 지상/지하 삭제

        // 6. [핵심] 숫자(지번/건물번호) 뒤에 붙은 불필요한 명칭 삭제
        // 예: 184-13 세일종합상가 -> 184-13만 남김
        // 도로명이나 동 이름 뒤에 숫자가 나오고, 그 뒤에 오는 한글/공백 패턴을 제거
        refined = refined.replaceAll("(\\d+(?:-\\d+)?)\\s+[가-힣].*", "$1");

        // 7. 연속 공백 정리 및 트림
        return refined.replaceAll("\\s{2,}", " ").trim();
    }
}