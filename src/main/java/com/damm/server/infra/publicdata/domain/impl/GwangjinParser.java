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
public class GwangjinParser implements PublicDataParser {
    @Override
    public boolean isSupport(District cityDistrict) {
        return District.GWANGJIN == cityDistrict;
    }

    @Override
    public URI createUri(String baseUrl, String apiKey, int pageNo, int numOfRows) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("pageNo", pageNo)
                .queryParam("serviceKey", apiKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("id", Optional.empty())
                .queryParam("type", "json")
                .build()
                .toUri();
    }

    @Override
    public PublicDataClient.PublicDataFetchResponse parse(JsonNode rootNode, ObjectMapper objectMapper) {
        List<SmokingAreaItem> items = new ArrayList<>();
        PublicDataMeta meta = null;

        // 광진구는 root 자체가 배열입니다.
        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                if (node.has("id")) {
                    Map<String, String> rawMap = new HashMap<>();

                    rawMap.put(SmokingAreaItem.KEY_ID, node.path("id").asText());
                    rawMap.put(SmokingAreaItem.흡연구역_명칭, node.path("area_nm").asText());
                    rawMap.put(SmokingAreaItem.설치_위치_상세, node.path("area_desc").asText());
                    rawMap.put(SmokingAreaItem.시도_명칭, node.path("ctprvnnm").asText());
                    rawMap.put(SmokingAreaItem.시군구_명칭, node.path("signgunm").asText());
                    rawMap.put(SmokingAreaItem.읍면동_명칭, node.path("emdnm").asText());
                    rawMap.put(SmokingAreaItem.흡연구역_구분, node.path("area_se").asText());
                    rawMap.put(SmokingAreaItem.면적, node.path("area_ar").asText());
                    rawMap.put(SmokingAreaItem.도로명_주소, node.path("rdnmadr").asText());
                    rawMap.put(SmokingAreaItem.지번_주소, node.path("lnmadr").asText());
                    rawMap.put(SmokingAreaItem.관리_기관_명칭, node.path("inst_nm").asText());
                    rawMap.put(SmokingAreaItem.위도, node.path("latitude").asText());
                    rawMap.put(SmokingAreaItem.경도, node.path("longitude").asText());
                    rawMap.put(SmokingAreaItem.시설_구분, node.path("fclty_knd").asText());
                    rawMap.put(SmokingAreaItem.데이터_기준_일자, node.path("ref_date").asText());

                    items.add(new SmokingAreaItem(rawMap));
                } else if (node.has("totalCount")) {
                    meta = objectMapper.convertValue(node, PublicDataMeta.class);
                }
            }
        }
        return new PublicDataClient.PublicDataFetchResponse(items, meta);
    }
}