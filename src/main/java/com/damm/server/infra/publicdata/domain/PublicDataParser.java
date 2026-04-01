package com.damm.server.infra.publicdata.domain;

import com.damm.server.infra.publicdata.PublicDataClient;
import com.damm.server.infra.publicdata.domain.enums.ParserType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;

public interface PublicDataParser {
    /**
     * 해당 파서가 지원하는 타입인지 확인합니다.
     */
    boolean isSupport(ParserType parserType);

    URI createUri(String baseUrl, String apiKey, int pageNo, int numOfRows);

    /**
     * 지역별 특수한 JSON 구조를 공통 응답 규격으로 변환합니다.
     */
    PublicDataClient.PublicDataFetchResponse parse(JsonNode rootNode, ObjectMapper objectMapper);
}