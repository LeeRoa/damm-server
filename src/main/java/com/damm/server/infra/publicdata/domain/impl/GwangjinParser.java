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
                    rawMap.put(SmokingAreaItem.KEY_AREA_NM, node.path("area_nm").asText());
                    rawMap.put(SmokingAreaItem.KEY_AREA_DESC, node.path("area_desc").asText());
                    rawMap.put(SmokingAreaItem.KEY_CTPRVNNM, node.path("ctprvnnm").asText());
                    rawMap.put(SmokingAreaItem.KEY_SIGNGUNM, node.path("signgunm").asText());
                    rawMap.put(SmokingAreaItem.KEY_EMDNM, node.path("emdnm").asText());
                    rawMap.put(SmokingAreaItem.KEY_AREA_SE, node.path("area_se").asText());
                    rawMap.put(SmokingAreaItem.KEY_AREA_AR, node.path("area_ar").asText());
                    rawMap.put(SmokingAreaItem.KEY_RDNMADR, node.path("rdnmadr").asText());
                    rawMap.put(SmokingAreaItem.KEY_LNMADR, node.path("lnmadr").asText());
                    rawMap.put(SmokingAreaItem.KEY_INST_NM, node.path("inst_nm").asText());
                    rawMap.put(SmokingAreaItem.KEY_LATITUDE, node.path("latitude").asText());
                    rawMap.put(SmokingAreaItem.KEY_LONGITUDE, node.path("longitude").asText());
                    rawMap.put(SmokingAreaItem.KEY_FCLTY_KND, node.path("fclty_knd").asText());
                    rawMap.put(SmokingAreaItem.KEY_REF_DATE, node.path("ref_date").asText());

                    items.add(new SmokingAreaItem(rawMap));
                } else if (node.has("totalCount")) {
                    meta = objectMapper.convertValue(node, PublicDataMeta.class);
                }
            }
        }
        return new PublicDataClient.PublicDataFetchResponse(items, meta);
    }
}