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
import java.util.Optional;

@Component
public class GwangjinParser implements PublicDataParser {
    @Override
    public boolean isSupport(ParserType parserType) {
        return ParserType.KOR_PUB_V2 == parserType;
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
                    items.add(objectMapper.convertValue(node, SmokingAreaItem.class));
                } else if (node.has("totalCount")) {
                    meta = objectMapper.convertValue(node, PublicDataMeta.class);
                }
            }
        }
        return new PublicDataClient.PublicDataFetchResponse(items, meta);
    }
}