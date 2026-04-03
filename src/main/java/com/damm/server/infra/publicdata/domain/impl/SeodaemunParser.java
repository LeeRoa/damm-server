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
public class SeodaemunParser implements PublicDataParser {
    @Override
    public boolean isSupport(String cityDistrict) {
        return "서대문구".equals(cityDistrict);
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
            rawMap.put(SmokingAreaItem.KEY_AREA_NM, node.path("설치위치 상세").asText());
            rawMap.put(SmokingAreaItem.KEY_AREA_DESC, node.path("설치위치 상세").asText());
            rawMap.put(SmokingAreaItem.KEY_CTPRVNNM, "서울특별시");
            rawMap.put(SmokingAreaItem.KEY_SIGNGUNM, node.path("자치구").asText());
            rawMap.put(SmokingAreaItem.KEY_AREA_SE, node.path("시설형태").asText());
            rawMap.put(SmokingAreaItem.KEY_AREA_AR, node.path("규모(㎡)").asText());
            rawMap.put(SmokingAreaItem.KEY_RDNMADR, node.path("설치위치").asText());
            rawMap.put(SmokingAreaItem.KEY_INST_NM, node.path("관리기관").asText());
            rawMap.put(SmokingAreaItem.KEY_REF_DATE, node.path("설치일").asText());

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