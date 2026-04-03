package com.damm.server.infra.kakao;

import com.damm.server.global.util.RestApiUtil;
import com.damm.server.infra.kakao.dto.GeocodingResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoGeocodingClient {

    private final RestApiUtil restApiUtil;

    @Value("${kakao.api.key}")
    private String kakaoApiKey; // REST API 키 (서버 통신용)

    @Value("${kakao.api.url}")
    private String kakaoApiUrl;

    public GeocodingResponse getGeocodingData(String address) {
        if (address == null || address.isBlank()) return null;

        try {
            URI uri = UriComponentsBuilder.fromUriString(kakaoApiUrl)
                    .queryParam("query", address)
                    .build()
                    .encode()
                    .toUri();

            Map<String, String> headers = Map.of("Authorization", "KakaoAK " + kakaoApiKey);

            JsonNode response = restApiUtil.get(uri, headers, new ParameterizedTypeReference<>() {
            });

            return extractGeocodingData(response, address);
        } catch (Exception e) {
            log.error("카카오 지오코딩 호출 실패 (주소: {}): {}", address, e.getMessage());
            return null;
        }
    }

    private GeocodingResponse extractGeocodingData(JsonNode response, String address) {
        JsonNode documents = response.path("documents");

        if (documents.isArray() && !documents.isEmpty()) {
            JsonNode first = documents.get(0);

            // 1. 좌표 추출 (x: 경도, y: 위도)
            double lon = first.path("x").asDouble();
            double lat = first.path("y").asDouble();

            // 2. 주소 정보 추출 (지번 및 읍면동)
            JsonNode addressNode = first.path("address");
            String lnmadr = addressNode.path("address_name").asText(); // 전체 지번 주소
            String emdnm = addressNode.path("region_3depth_name").asText(); // 읍면동명

            log.debug("[지오코딩 성공] 주소: {}, 좌표: {}, {}", address, lat, lon);
            return new GeocodingResponse(lat, lon, lnmadr, emdnm);
        }

        log.warn("[지오코딩 결과 없음] 주소: {}", address);
        return null;
    }
}