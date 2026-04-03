package com.damm.server.infra.kakao;

import com.damm.server.global.util.RestApiUtil;
import com.damm.server.infra.kakao.dto.GeocodingResponse;
import com.damm.server.infra.publicdata.domain.enums.District;
import com.damm.server.infra.publicdata.domain.enums.Province;
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

    private GeocodingResponse extractGeocodingData(JsonNode response, String originalAddress) {
        JsonNode documents = response.path("documents");

        if (documents.isArray() && !documents.isEmpty()) {
            JsonNode first = documents.get(0);

            // 좌표 추출 (x: 경도, y: 위도)
            double lon = first.path("x").asDouble();
            double lat = first.path("y").asDouble();

            // 도로명 주소 추출 (road_address 노드가 없으면 빈 문자열)
            JsonNode roadAddressNode = first.path("road_address");
            String rdnmadr = roadAddressNode.path("address_name").asText();

            // 지번 및 행정구역 정보 추출
            JsonNode addressNode = first.path("address");
            String lnmadr = addressNode.path("address_name").asText();
            String emdnm = addressNode.path("region_3depth_name").asText();

            // 이넘 변환을 위한 지역 명칭 추출
            String region1 = addressNode.path("region_1depth_name").asText(); // 시도 (예: 서울특별시)
            String region2 = addressNode.path("region_2depth_name").asText(); // 시군구 (예: 중랑구)

            // 기존에 정의한 find() 메서드로 ENUM 변환
            Province province = Province.find(region1);
            District district = District.find(region2);

            log.debug("[지오코딩 성공] 원본: {}, 도로명: {}, 좌표: {}, {}",
                    originalAddress, rdnmadr, lat, lon);

            return new GeocodingResponse(lat, lon, rdnmadr, lnmadr, province, district, emdnm);
        }

        log.warn("[지오코딩 결과 없음] 주소: {}", originalAddress);
        return null;
    }
}