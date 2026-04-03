package com.damm.server.infra.publicdata.domain.impl;

import com.damm.server.infra.publicdata.PublicDataClient;
import com.damm.server.infra.publicdata.domain.PublicDataParser;
import com.damm.server.infra.publicdata.dto.PublicDataMeta;
import com.damm.server.infra.publicdata.dto.SmokingAreaItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SongpaParser implements PublicDataParser {
    @Override
    public boolean isSupport(String cityDistrict) {
        return "송파구".equals(cityDistrict);
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
            Map<String, String> rawMap = new HashMap<>();

            rawMap.put(SmokingAreaItem.KEY_ID, String.valueOf(node.path("연번").asInt()));
            rawMap.put(SmokingAreaItem.KEY_AREA_NM, node.path("건물명").asText());
            rawMap.put(SmokingAreaItem.KEY_AREA_DESC, node.path("건물명").asText());
            rawMap.put(SmokingAreaItem.KEY_CTPRVNNM, "서울특별시");
            rawMap.put(SmokingAreaItem.KEY_SIGNGUNM, "송파구");
            rawMap.put(SmokingAreaItem.KEY_AREA_SE, node.path("구분").asText());
            rawMap.put(SmokingAreaItem.KEY_RDNMADR, node.path("도로명주소").asText());
            rawMap.put(SmokingAreaItem.KEY_INST_NM, "송파구청");
            rawMap.put(SmokingAreaItem.KEY_REF_DATE, node.path("데이터기준일자").asText());

            items.add(new SmokingAreaItem(rawMap));
        }

        PublicDataMeta meta = new PublicDataMeta(
                rootNode.path("totalCount").asInt(),
                rootNode.path("page").asText(),
                rootNode.path("perPage").asText()
        );

        return new PublicDataClient.PublicDataFetchResponse(items, meta);
    }
}