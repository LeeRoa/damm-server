package com.damm.server.infra.publicdata.domain.impl;

import com.damm.server.infra.publicdata.PublicDataClient;
import com.damm.server.infra.publicdata.domain.PublicDataParser;
import com.damm.server.infra.publicdata.domain.enums.ParserType;
import com.damm.server.infra.publicdata.dto.PublicDataMeta;
import com.damm.server.infra.publicdata.dto.SmokingAreaItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class SeodaemunParser implements PublicDataParser {
    @Override
    public boolean isSupport(ParserType parserType) {
        return ParserType.KOR_PUB_V3 == parserType;
    }

    @Override
    public URI createUri(String baseUrl, String apiKey, int pageNo, int numOfRows) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("page", pageNo)
                .queryParam("perPage", numOfRows)
                .queryParam("returnType", "JSON")
                .build()
                .toUri();
    }

    @Override
    public PublicDataClient.PublicDataFetchResponse parse(JsonNode rootNode, ObjectMapper objectMapper) {
        JsonNode dataNode = rootNode.path("data");
        List<SmokingAreaItem> items = new ArrayList<>();

        for (JsonNode node : dataNode) {
            items.add(new SmokingAreaItem(
                    String.valueOf(node.path("연번").asInt()),    // 1. id (PK)
                    node.path("설치위치 상세").asText(),                 // 2. areaNm (흡연구역 명)
                    node.path("설치위치 상세").asText(),           // 3. areaDesc (흡연구역 상세 위치)
                    "서울특별시",                                    // 4. ctprvnnm (시도명)
                    node.path("자치구").asText(),                  // 5. signgunm (시군구명)
                    null,                                          // 6. emdnm (읍면동명, 지오코딩 보정 대상)
                    node.path("시설형태").asText(),                  // 7. areaSe (흡연구역 구분)
                    node.path("규모(㎡)").asText(),              // 8. areaAr (흡연구역 면적)
                    node.path("설치위치").asText(),                // 9. rdnmadr (소재지 도로명주소)
                    null,                                           // 10. lnmadr (시군구명, 지오코딩 보정 대상)
                    node.path("관리기관").asText(),                 // 11. instNm (관리기관 명)
                    null,                                           // 12. latitude (위도, 지오코딩 보정 대상)
                    null,                                           // 13. longitude (경도, 지오코딩 보정 대상)
                    null,                                     // 14. fcltyKnd (이미지 경로 - 데이터 없음)
                    node.path("설치일").asText()              // 15. refDate (데이터 기준 일자)
            ));
        }

        PublicDataMeta meta = new PublicDataMeta(
                rootNode.path("totalCount").asInt(),
                rootNode.path("page").asText(),
                rootNode.path("perPage").asText()
        );

        return new PublicDataClient.PublicDataFetchResponse(items, meta);
    }
}