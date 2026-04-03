package com.damm.server.infra.publicdata.domain.impl;

import com.damm.server.infra.publicdata.PublicDataClient;
import com.damm.server.infra.publicdata.domain.PublicDataParser;
import com.damm.server.infra.publicdata.domain.enums.District;
import com.damm.server.infra.publicdata.dto.PublicDataMeta;
import com.damm.server.infra.publicdata.dto.SmokingAreaItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Component
public class JungParser implements PublicDataParser {

    @Override
    public boolean isSupport(District cityDistrict) {
        return District.JUNG == cityDistrict;
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

            rawMap.put(SmokingAreaItem.KEY_ID, UUID.randomUUID().toString());
            rawMap.put(SmokingAreaItem.흡연구역_구분, node.path("구분").asText());
            rawMap.put(SmokingAreaItem.면적, node.path("규모").asText());
            rawMap.put(SmokingAreaItem.도로명_주소, node.path("설치도로명주소").asText());
            rawMap.put(SmokingAreaItem.흡연구역_명칭, node.path("설치위치").asText());
            rawMap.put(SmokingAreaItem.설치_위치_상세, node.path("설치위치").asText());
            rawMap.put(SmokingAreaItem.시도_명칭, District.JUNG.getProvince().getKoreanName());
            rawMap.put(SmokingAreaItem.시군구_명칭, District.JUNG.getKoreanName());
            rawMap.put(SmokingAreaItem.데이터_기준_일자, node.path("데이터기준일자").asText());
            rawMap.put(SmokingAreaItem.관리_기관_명칭, node.path("운영관리").asText());

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