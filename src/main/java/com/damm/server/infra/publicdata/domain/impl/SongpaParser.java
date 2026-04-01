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
public class SongpaParser implements PublicDataParser {
    @Override
    public boolean isSupport(ParserType parserType) {
        return ParserType.KOR_PUB_V1 == parserType;
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
            // 송파구 데이터의 한글 키값을 SmokingAreaItem 레코드 규격에 맞춰 매핑한다.
            items.add(new SmokingAreaItem(
                    String.valueOf(node.path("연번").asInt()), // 1. id
                    node.path("건물명").asText(),              // 2. areaNm
                    node.path("건물명").asText(),          // 3. areaDesc (상세가 없어서 대체함)
                    "서울특별시",                             // 4. ctprvnnm
                    "송파구",                                 // 5. signgunm
                    null,                                    // 6. emdnm (지오코딩 보정 대상)
                    node.path("구분").asText(),               // 7. areaSe
                    null,                                    // 8. areaAr
                    node.path("도로명주소").asText(),          // 9. rdnmadr
                    null,                                    // 10. lnmadr (지오코딩 보정 대상)
                    "송파구청",                               // 11. instNm
                    null,                                    // 12. latitude (지오코딩 보정 대상)
                    null,                                    // 13. longitude (지오코딩 보정 대상)
                    null,                                     // 14. fcltyKnd (이미지 경로 - 데이터 없음)
                    node.path("데이터기준일자").asText()       // 15. refDate
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