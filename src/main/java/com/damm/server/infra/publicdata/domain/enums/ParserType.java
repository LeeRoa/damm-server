package com.damm.server.infra.publicdata.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParserType {
    GWANGJIN("광진구 스타일 - 혼합 배열형"),
    SEODAEMUN("서대문구 스타일 - 객체 데이터형"),
    SONGPA("송파구 스타일 - 한글 키값 객체형");

    private final String description;
}