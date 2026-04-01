package com.damm.server.infra.publicdata;

import com.damm.server.global.util.RestApiUtil;
import com.damm.server.infra.publicdata.domain.ApiSource;
import com.damm.server.infra.publicdata.domain.PublicDataParser;
import com.damm.server.infra.publicdata.dto.PublicDataMeta;
import com.damm.server.infra.publicdata.dto.SmokingAreaItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * 공공데이터포털 API와 통신하는 클라이언트다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataClient {

    private final RestApiUtil restApiUtil;
    private final ObjectMapper objectMapper;

    // 스프링이 PublicDataParser 인터페이스를 구현한 모든 빈(Gwangjin, Songpa 등)을 리스트로 주입해준다.
    private final List<PublicDataParser> parsers;

    @Value("${public-data.api.key}")
    private String apiKey;

    public PublicDataFetchResponse fetchSmokingAreas(ApiSource source, int pageNo, int numOfRows) {
        PublicDataParser parser = parsers.stream()
                .filter(p -> p.isSupport(source.getParserType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("지원하지 않는 파서 타입입니다."));

        URI finalUri = parser.createUri(source.getBaseUrl(), apiKey, pageNo, numOfRows);
        log.info("[API 요청] 지역: {}, URI: {}", source.getRegionName(), finalUri);

        JsonNode responseNode = restApiUtil.get(
                finalUri,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if (responseNode == null || responseNode.isMissingNode()) return null;

        // 4. 파서에게 결과 파싱을 요청한다.
        return parser.parse(responseNode, objectMapper);
    }

    public record PublicDataFetchResponse(List<SmokingAreaItem> items, PublicDataMeta meta) {}
}