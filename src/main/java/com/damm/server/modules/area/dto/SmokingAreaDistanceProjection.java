package com.damm.server.modules.area.dto;

public interface SmokingAreaDistanceProjection {
    // 1. 식별 및 기본 정보
    Long getInternalId();       // DB PK 추가
    String getId();             // API ID
    String getAreaNm();         // 흡연구역 명칭 (리스트 제목)
    String getAreaDesc();       // 상세 설명 (예: "건물 뒤편 주차장")
    String getAreaSe();         // 구역 구분 (개방형, 폐쇄형 등 - 필터링 및 아이콘 표시용)

    // 2. 위치 및 경로 정보
    String getRawAddress();     // 전체 주소
    Double getLatitude();       // 위도 (Y)
    Double getLongitude();      // 경도 (X)
    Double getDistance();       // 내 위치와의 거리 (미터 단위)

    // 3. 부가 정보 (맛집 리스트의 '사진' 및 '영업상태' 역할)
    String getFcltyKnd();       // 시설 이미지 URL (또는 시설 종류)
    String getStatus();         // 운영 상태 (OPEN, CLOSED 등)
    String getInstNm();         // 관리 기관 (선택 사항)
}