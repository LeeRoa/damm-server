package com.damm.server.infra.publicdata.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParserType {
    KOR_PUB_V1("공공데이터 포털 - 한글 키/주소 중심 (ex. 송파구)"),
    KOR_PUB_V2("공공데이터 포털 - 영문 키/좌표 및 이미지 포함 (ex. 광진구)"),
    KOR_PUB_V3("공공데이터 포털 - 한글 키/상세 위치 특화 (ex. 서대문구)"),
    SEOUL_OPEN_API("서울시 열린데이터 광장 전용 규격");

    private final String description;
}